package org.quering;

import org.analyzing.Token;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.reading.IndexReader;
import org.scoring.ScoreResult;
import org.scoring.calculation.ScoreCalculator;
import org.storage.invertedIndex.PostingList;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public class QueryProcessor {
    @NonNull
    private AnalyzerStrategy analyzer;
    @NonNull
    private IndexReader indexReader;
    @NonNull
    private ScoreCalculator scoreCalculator;

    private Set<String> findDocsOfField(Map<String, String> conditions) throws IOException {
        log.debug("Finding matching documents for query conditions: fieldCount {}", conditions.size());
        Set<String> matchDocs = new TreeSet<>();
        for (String fieldName : conditions.keySet()) {
            log.debug("Analyzing query condition for field: {}", fieldName);
            List<Token> queryFieldTokens = analyzer.getAnalyzer(fieldName)
                    .analyze(conditions.get(fieldName));
            log.debug("Query field analyzed: field {}, tokenCount {}", fieldName, queryFieldTokens.size());

            Map<String, PostingList> postingsTerms = indexReader.getPosting(fieldName);
            if (postingsTerms == null) {
                log.warn("Skipping field because postings were not found: field {}", fieldName);
                continue;
            }
            Map<String, PostingList> postingsTermsInQuery = postingsTerms.entrySet().stream()
                    .filter(entry -> queryFieldTokens.stream().anyMatch(token ->
                            entry.getKey().equals(token.term())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            log.debug("Postings matched query tokens: field {}, matchedTermCount {}", fieldName, postingsTermsInQuery.size());

            matchDocs.addAll(new HashSet(postingsTermsInQuery.values().stream()
                    .map(entry -> entry.getPostings().keySet())
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet())));
        }
        log.debug("Matching documents found: count {}", matchDocs.size());
        return matchDocs;
    }

    public List<ScoreResult> process(Query query) throws IOException {
        log.info("Processing query: queryId {}, queryType {}, limit {}, start {}",
                query.getQueryId(), query.getQueryType(), query.getLimit(), query.getStart());
        Map<String, String> conditions = query.getConditions();
        List<ScoreResult> results = new ArrayList<>();
        Set<String> matchDocs = findDocsOfField(conditions);
        log.debug("Building query tokens by field: queryId {}, fieldCount {}", query.getQueryId(), conditions.size());
        Map<String, List<String>> fieldsTokensQuery = conditions.keySet().stream().collect(Collectors.toMap(
                                fieldName -> fieldName,
                                fieldName -> {
                                    try {
                                        log.debug("Analyzing query tokens for scoring: queryId {}, field {}", query.getQueryId(), fieldName);
                                        return analyzer.getAnalyzer(fieldName)
                                                .analyze(conditions.get(fieldName)).stream()
                        .map(Token::term)
                        .collect(Collectors.toList());
                                    } catch (IOException e) {
                                        log.error("Failed to analyze query field: queryId {}, field {}", query.getQueryId(), fieldName, e);
                                        throw new RuntimeException(e);
                                    }
                                }));
        matchDocs.forEach(doc -> {
            log.debug("Scoring document for query: queryId {}, docId {}", query.getQueryId(), doc);
            results.add(scoreCalculator.calculateScores(indexReader.buildContext(doc,fieldsTokensQuery)));
        });
        log.info("Query processed: queryId {}, matchedDocs {}, results {}", query.getQueryId(), matchDocs.size(), results.size());
        return results;
    }
}
