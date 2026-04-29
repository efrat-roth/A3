package quering;

import org.analyzing.Analyzer;
import org.analyzing.Token;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.junit.jupiter.api.Test;
import org.quering.Query;
import org.quering.QueryProcessor;
import org.reading.IndexReader;
import org.reading.QueryContext;
import org.scoring.ScoreResult;
import org.scoring.calculation.ScoreCalculator;
import org.storage.invertedIndex.PostingList;
import org.storage.invertedIndex.TermStats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QueryProcessorTest {

    @Test
    void processShouldScoreDocumentsThatContainAnalyzedQueryTerms() throws IOException {
        AnalyzerStrategy analyzerStrategy = field -> analyzerReturningInputAsToken();
        IndexReader indexReader = mock(IndexReader.class);
        ScoreCalculator scoreCalculator = mock(ScoreCalculator.class);
        QueryProcessor processor = new QueryProcessor(analyzerStrategy, indexReader, scoreCalculator);
        Query query = new Query("q1", Map.of("title", "hello"), 10, 0, null);
        QueryContext doc1Context = queryContext("doc-1");
        QueryContext doc2Context = queryContext("doc-2");

        when(indexReader.getPosting("title")).thenReturn(Map.of(
                "hello", postingList("doc-2", "doc-1"),
                "other", postingList("doc-3")
        ));
        when(indexReader.buildContext(eq("doc-1"), eq(Map.of("title", List.of("hello")))))
                .thenReturn(doc1Context);
        when(indexReader.buildContext(eq("doc-2"), eq(Map.of("title", List.of("hello")))))
                .thenReturn(doc2Context);
        when(scoreCalculator.calculateScores(doc1Context)).thenReturn(new ScoreResult("doc-1", 2.0));
        when(scoreCalculator.calculateScores(doc2Context)).thenReturn(new ScoreResult("doc-2", 1.5));

        List<ScoreResult> results = processor.process(query);

        assertThat(results).containsExactly(
                new ScoreResult("doc-1", 2.0),
                new ScoreResult("doc-2", 1.5)
        );
    }

    @Test
    void processShouldUseAnalyzedTokensGroupedByFieldWhenBuildingContext() throws IOException {
        AnalyzerStrategy analyzerStrategy = field -> {
            if ("title".equals(field)) {
                return analyzerReturning(new Token("java", 1));
            }
            return analyzerReturning(new Token("index", 1), new Token("search", 2));
        };
        IndexReader indexReader = mock(IndexReader.class);
        ScoreCalculator scoreCalculator = mock(ScoreCalculator.class);
        QueryProcessor processor = new QueryProcessor(analyzerStrategy, indexReader, scoreCalculator);
        Query query = new Query("q1", new HashMap<>(Map.of(
                "title", "ignored title",
                "body", "ignored body"
        )), 10, 0, null);
        QueryContext context = queryContext("doc-1");

        when(indexReader.getPosting("title")).thenReturn(Map.of("java", postingList("doc-1")));
        when(indexReader.getPosting("body")).thenReturn(Map.of(
                "index", postingList("doc-1"),
                "search", postingList("doc-2")
        ));
        when(indexReader.buildContext(eq("doc-1"), eq(Map.of(
                "title", List.of("java"),
                "body", List.of("index", "search")
        )))).thenReturn(context);
        when(indexReader.buildContext(eq("doc-2"), eq(Map.of(
                "title", List.of("java"),
                "body", List.of("index", "search")
        )))).thenReturn(queryContext("doc-2"));
        when(scoreCalculator.calculateScores(context)).thenReturn(new ScoreResult("doc-1", 3.0));
        when(scoreCalculator.calculateScores(any(QueryContext.class)))
                .thenAnswer(invocation -> {
                    QueryContext queryContext = invocation.getArgument(0);
                    return new ScoreResult(queryContext.getDocId(), 1.0);
                });

        List<ScoreResult> results = processor.process(query);

        assertThat(results).extracting(ScoreResult::docId).containsExactly("doc-1", "doc-2");
        verify(indexReader).buildContext("doc-1", Map.of(
                "title", List.of("java"),
                "body", List.of("index", "search")
        ));
    }

    @Test
    void processShouldReturnEmptyListWhenNoQueryTermsMatchPostings() throws IOException {
        AnalyzerStrategy analyzerStrategy = field -> analyzerReturningInputAsToken();
        IndexReader indexReader = mock(IndexReader.class);
        ScoreCalculator scoreCalculator = mock(ScoreCalculator.class);
        QueryProcessor processor = new QueryProcessor(analyzerStrategy, indexReader, scoreCalculator);
        Query query = new Query("q1", Map.of("title", "missing"), 10, 0, null);

        when(indexReader.getPosting("title")).thenReturn(Map.of("hello", postingList("doc-1")));

        List<ScoreResult> results = processor.process(query);

        assertThat(results).isEmpty();
        verify(indexReader, never()).buildContext(any(), any());
        verify(scoreCalculator, never()).calculateScores(any());
    }

    @Test
    void processShouldPropagateIOExceptionFromAnalyzer() {
        AnalyzerStrategy analyzerStrategy = field -> Analyzer.builder()
                .charFilters(List.of(input -> input))
                .tokenizer(input -> List.of(new Token(input, 1)))
                .tokenFilters(List.of(tokens -> {
                    throw new IOException("analyzer failed");
                }))
                .build();
        QueryProcessor processor = new QueryProcessor(
                analyzerStrategy,
                mock(IndexReader.class),
                mock(ScoreCalculator.class)
        );
        Query query = new Query("q1", Map.of("title", "hello"), 10, 0, null);

        assertThatThrownBy(() -> processor.process(query))
                .isInstanceOf(IOException.class)
                .hasMessage("analyzer failed");
    }

    private static Analyzer analyzerReturningInputAsToken() {
        return Analyzer.builder()
                .charFilters(List.of())
                .tokenizer(input -> List.of(new Token(input, 1)))
                .tokenFilters(List.of())
                .build();
    }

    private static Analyzer analyzerReturning(Token... tokens) {
        return Analyzer.builder()
                .charFilters(List.of())
                .tokenizer(input -> List.of(tokens))
                .tokenFilters(List.of())
                .build();
    }

    private static PostingList postingList(String... docIds) {
        Map<String, TermStats> postings = new HashMap<>();
        for (String docId : docIds) {
            postings.put(docId, new TermStats(1.0, new ArrayList<>(List.of(1))));
        }
        return new PostingList(postings);
    }

    private static QueryContext queryContext(String docId) {
        return new QueryContext(docId, List.of(), Map.of(), 1);
    }
}
