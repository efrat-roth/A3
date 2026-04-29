package org.quering;

import org.analyzing.Token;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import lombok.Data;
import lombok.NonNull;
import org.reading.IndexReader;
import org.scoring.ScoreResult;
import org.scoring.calculation.ScoreCalculator;
import org.storage.invertedIndex.PostingList;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class QueryProcessor {
    @NonNull
    private AnalyzerStrategy analyzer;
    @NonNull
    private IndexReader indexReader;
    @NonNull
    private ScoreCalculator scoreCalculator;

    private Set<String> findDocsOfField(Map<String, String> conditions) throws IOException {
        Set<String> matchDocs = new TreeSet<>();
        for (String fieldName : conditions.keySet()) {
            List<Token> queryFieldTokens = analyzer.getAnalyzer(fieldName)
                    .analyze(conditions.get(fieldName));

            Map<String, PostingList> postingsTerms = indexReader.getPosting(fieldName);
            Map<String, PostingList> postingsTermsInQuery = postingsTerms.entrySet().stream()
                    .filter(entry -> queryFieldTokens.stream().anyMatch(token ->
                            entry.getKey().equals(token.term())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            matchDocs.addAll(new HashSet(postingsTermsInQuery.values().stream()
                    .map(entry -> entry.getPostings().keySet())
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet())));
        }
        return matchDocs;
    }

    public List<ScoreResult> process(Query query) throws IOException {
        Map<String, String> conditions = query.getConditions();
        List<ScoreResult> results = new ArrayList<>();
        Set<String> matchDocs = findDocsOfField(conditions);
        Map<String, List<String>> fieldsTokensQuery = conditions.keySet().stream().collect(Collectors.toMap(
                                fieldName -> fieldName,
                                fieldName -> {
                                    try {
                                        return analyzer.getAnalyzer(fieldName)
                                                .analyze(conditions.get(fieldName)).stream()
                        .map(Token::term)
                        .collect(Collectors.toList());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }));
        matchDocs.forEach(doc -> results.add(scoreCalculator.calculateScores(indexReader.buildContext(doc,fieldsTokensQuery))));
        return results;
    }
}
