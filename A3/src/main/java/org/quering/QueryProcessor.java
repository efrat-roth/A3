package org.quering;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Token;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.reading.IndexReader;
import org.scoring.ScoreResult;
import org.scoring.calculation.ScoreCalculator;
import org.storage.invertedIndex.PostingList;
import org.utils.Exceptions;

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

    public List<ScoreResult> process(Query query) {

        validateQuery(query);
        try {
            log.info("Processing query: queryId {}, queryType {}, limit {}, start {}",
                    query.getQueryId(), query.getQueryType(), query.getLimit(), query.getStart());

            Map<String, String> conditions = query.getConditions();
            List<ScoreResult> results = new ArrayList<>();

            Set<String> matchDocs = findDocsOfField(conditions);

            Map<String, List<String>> fieldsTokensQuery = buildQueryTokens(query, conditions);

            matchDocs.forEach(doc -> results.add(scoreCalculator.calculateScores(
                    indexReader.buildContext(doc, fieldsTokensQuery))));

            log.info("Query processed: queryId {}, matchedDocs {}, results {}",
                    query.getQueryId(), matchDocs.size(), results.size());

            return results.stream().sorted(Comparator.comparingDouble(ScoreResult::totalScore).reversed())
                    .skip(query.getStart()).limit(query.getLimit()).toList();

        } catch (RuntimeException e) {
            log.error("Query processing failed: queryId {}", query.getQueryId(), e);

            throw new Exceptions.QueryProcessingException("Failed to process query: " + query.getQueryId(), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, List<String>> buildQueryTokens(Query query, Map<String, String> conditions) {
        return conditions.keySet().stream()
                .collect(Collectors.toMap(field -> field, field -> analyzeField(query, field)));
    }

    private List<String> analyzeField(Query query, String fieldName) {

        try {
            return analyzer.getAnalyzer(fieldName).analyze(query.getConditions().get(fieldName)).stream()
                    .map(Token::term).collect(Collectors.toList());

        } catch (IOException e) {
            throw new Exceptions.QueryProcessingException("Failed analyzing field: " + fieldName, e);
        }
    }

    private void validateQuery(Query query) {

        if (query == null) {
            throw new Exceptions.InvalidQueryException("Query cannot be null");
        }

        if (query.getConditions() == null || query.getConditions().isEmpty()) {
            throw new Exceptions.InvalidQueryException("Query conditions cannot be empty");
        }
    }
}
