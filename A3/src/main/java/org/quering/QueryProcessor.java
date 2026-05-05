package org.quering;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Token;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.reading.IndexReader;
import org.scoring.ScoreResult;
import org.scoring.calculation.ScoreCalculator;
import org.storage.Field;
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

    private Set<String> findDocsOfField(Map<String, List<String>> conditionsQuery) throws IOException {
        log.debug("Finding matching documents for query conditions: fieldCount {}", conditionsQuery.size());
        Set<String> matchDocs = new HashSet<>();
        for (String fieldName : conditionsQuery.keySet()) {

            log.debug("Analyzing query condition for field: {}", fieldName);

            List<String> queryFieldTokens = conditionsQuery.get(fieldName);

            log.debug("Query field analyzed: field {}, tokenCount {}", fieldName, queryFieldTokens.size());

            Map<String, PostingList> postingsTerms = indexReader.getPosting(fieldName);
            if (postingsTerms == null) {
                log.warn("Skipping field because postings were not found: field {}", fieldName);
            }
            Map<String, PostingList> postingsTermsInQuery = new HashMap<>();
            if (postingsTerms == null) continue;
            for (String term : queryFieldTokens) {
                PostingList pl = postingsTerms.get(term);
                if (pl != null) {
                    postingsTermsInQuery.put(term, pl);
                }

            }
            log.debug("Postings matched query tokens: field {}, matchedTermCount {}", fieldName, postingsTermsInQuery.size());

            for (PostingList pl : postingsTermsInQuery.values()) {
                matchDocs.addAll(pl.getPostings().keySet());
            }
        }
        log.debug("Matching documents found: count {}", matchDocs.size());
        return matchDocs;
    }

    public Map<String, Map<List<Field>, Double>> process(Query query) {

        validateQuery(query);
        try {
            log.info("Processing query: queryId {}, queryType {}, limit {}, start {}",
                    query.getQueryId(), query.getQueryType(), query.getLimit(), query.getStart());

            Map<String, String> conditions = query.getConditions();
            List<ScoreResult> results = new ArrayList<>();

            Map<String, List<String>> queryTermsByField = buildQueryTerms(conditions);
            Set<String> matchDocs = findDocsOfField(queryTermsByField);

            matchDocs.forEach(doc -> {
                results.add(scoreCalculator.calculateScores(
                        indexReader.buildContext(doc, queryTermsByField)));
            });

            log.info("Query processed: queryId {}, matchedDocs {}, results {}",
                    query.getQueryId(), matchDocs.size(), results.size());

            List<ScoreResult> resultDocs = results.stream().sorted(Comparator.comparingDouble(ScoreResult::totalScore)
                    .reversed()).skip(query.getStart()).limit(query.getLimit()).toList();

            return resultDocs.stream().collect(Collectors.toMap(
                    ScoreResult::docId,
                    scoreResult -> Map.of(
                            indexReader.getDocument(scoreResult.docId()), scoreResult.totalScore())));
        } catch (RuntimeException e) {
            log.error("Query processing failed: queryId {}", query.getQueryId(), e);

            throw new Exceptions.QueryProcessingException("Failed to process query: " + query.getQueryId(), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    private Map<String, List<String>> buildQueryTerms(Map<String, String> conditions) throws IOException {

        Map<String, List<String>> queryTerms = new HashMap<>();

        for (Map.Entry<String, String> entry : conditions.entrySet()) {

            String fieldName = entry.getKey();
            String rawQuery = entry.getValue();

            List<String> terms = analyzer.getAnalyzer(fieldName).analyze(rawQuery)
                    .stream().map(Token::term).toList();

            queryTerms.put(fieldName, terms);
        }

        return queryTerms;
    }


}
