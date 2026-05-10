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
import org.storage.FieldType;
import org.storage.invertedIndex.PostingList;
import org.storage.invertedIndex.TermStats;
import org.utils.Exceptions;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

        List<FieldType> doc1 = List.of(new FieldType("title", true));
        List<FieldType> doc2 = List.of(new FieldType("title", true));

        when(indexReader.getPosting("title")).thenReturn(Map.of(
                "hello", postingList("doc-2", "doc-1")
        ));

        when(indexReader.buildContext(eq("doc-1"), any()))
                .thenReturn(doc1Context);

        when(indexReader.buildContext(eq("doc-2"), any()))
                .thenReturn(doc2Context);

        when(indexReader.getDocument("doc-1")).thenReturn(doc1);
        when(indexReader.getDocument("doc-2")).thenReturn(doc2);

        when(scoreCalculator.calculateScores(doc1Context))
                .thenReturn(new ScoreResult("doc-1", 2.0));

        when(scoreCalculator.calculateScores(doc2Context))
                .thenReturn(new ScoreResult("doc-2", 1.5));

        Map<List<FieldType>, Double> results = processor.process(query);

        assertThat(results)
                .hasSize(2)
                .containsEntry(doc1, 2.0)
                .containsEntry(doc2, 1.5);
    }

    @Test
    void processShouldUseAnalyzedTokensGroupedByFieldWhenBuildingContext()
            throws IOException {

        AnalyzerStrategy analyzerStrategy = field -> {
            if ("title".equals(field)) {
                return analyzerReturning(new Token("java", 1));
            }

            return analyzerReturning(
                    new Token("index", 1),
                    new Token("search", 2)
            );
        };

        IndexReader indexReader = mock(IndexReader.class);
        ScoreCalculator scoreCalculator = mock(ScoreCalculator.class);

        QueryProcessor processor =
                new QueryProcessor(analyzerStrategy, indexReader, scoreCalculator);

        Query query = new Query(
                "q1",
                Map.of(
                        "title", "ignored",
                        "body", "ignored"
                ),
                10,
                0,
                null
        );

        QueryContext context = queryContext("doc-1");

        List<FieldType> doc1 = List.of(new FieldType("title", true));
        List<FieldType> doc2 = List.of(new FieldType("body", true));

        when(indexReader.getPosting("title"))
                .thenReturn(Map.of("java", postingList("doc-1")));

        when(indexReader.getPosting("body"))
                .thenReturn(Map.of(
                        "index", postingList("doc-1"),
                        "search", postingList("doc-2")
                ));

        when(indexReader.buildContext(eq("doc-1"), any()))
                .thenReturn(context);

        when(indexReader.buildContext(eq("doc-2"), any()))
                .thenReturn(queryContext("doc-2"));

        when(indexReader.getDocument("doc-1")).thenReturn(doc1);
        when(indexReader.getDocument("doc-2")).thenReturn(doc2);

        when(scoreCalculator.calculateScores(any()))
                .thenAnswer(invocation -> {
                    QueryContext qc = invocation.getArgument(0);
                    return new ScoreResult(qc.getDocId(), 1.0);
                });

        Map<List<FieldType>, Double> results = processor.process(query);

        assertThat(results).hasSize(2);

        verify(indexReader).buildContext(
                eq("doc-1"),
                eq(Map.of(
                        "title", List.of("java"),
                        "body", List.of("index", "search")
                ))
        );
    }

    @Test
    void processShouldReturnEmptyMapWhenNoQueryTermsMatchPostings()
            throws IOException {

        AnalyzerStrategy analyzerStrategy = field -> analyzerReturningInputAsToken();
        IndexReader indexReader = mock(IndexReader.class);
        ScoreCalculator scoreCalculator = mock(ScoreCalculator.class);

        QueryProcessor processor =
                new QueryProcessor(analyzerStrategy, indexReader, scoreCalculator);

        Query query =
                new Query("q1", Map.of("title", "missing"), 10, 0, null);

        when(indexReader.getPosting("title"))
                .thenReturn(Map.of("hello", postingList("doc-1")));

        Map<List<FieldType>, Double> results = processor.process(query);

        assertThat(results).isEmpty();

        verify(indexReader, never()).buildContext(any(), any());
        verify(scoreCalculator, never()).calculateScores(any());
    }

    @Test
    void processShouldWrapAnalyzerException() {

        AnalyzerStrategy analyzerStrategy = field -> Analyzer.builder()
                .charFilters(List.of(input -> input))
                .tokenizer(input -> List.of(new Token(input, 1)))
                .tokenFilters(List.of(tokens -> {
                    throw new IOException("analyzer failed");
                }))
                .build();

        QueryProcessor processor =
                new QueryProcessor(
                        analyzerStrategy,
                        mock(IndexReader.class),
                        mock(ScoreCalculator.class)
                );

        Query query =
                new Query("q1", Map.of("title", "hello"), 10, 0, null);

        assertThatThrownBy(() -> processor.process(query))
                .isInstanceOf(Exceptions.QueryProcessingException.class)
                .hasMessageContaining("Failed to process query");
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
            postings.put(
                    docId,
                    new TermStats(
                            1.0,
                            new ArrayList<>(List.of(1))
                    )
            );
        }

        return new PostingList(postings);
    }

    private static QueryContext queryContext(String docId) {
        return new QueryContext(docId, List.of(), Map.of(), 1);
    }
}