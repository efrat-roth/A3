package quering;

import analyzing.Token;
import analyzing.analyzerStrategy.AnalyzerStrategy;
import lombok.Data;
import lombok.NonNull;
import reading.IndexReader;
import scoring.ScoreResult;
import scoring.calculation.ScoreCalculator;
import storage.invertedIndex.PostingList;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class QueryProcessor {
    @NonNull
    private AnalyzerStrategy analyzer;
    @NonNull
    private IndexReader indexReader;
    @NonNull
    private ScoreCalculator scoreCalculator;

    private Set<String> findDocsOfField(Map<String, String> conditions) {
        Set<String> matchDocs = new TreeSet<>();
        for (String fieldName : conditions.keySet()) {
            //לתקן עם הקריאה ל null
            List<Token> queryFieldTokens = analyzer.getAnalyzer(fieldName, null)
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

    public List<ScoreResult> process(Query query) {
        Map<String, String> conditions = query.getConditions();
        List<ScoreResult> results = new ArrayList<>();
        Set<String> matchDocs = findDocsOfField(conditions);
        Map<String, List<String>> fieldsTokensQuery = conditions.keySet().stream().collect(Collectors.toMap(
                                fieldName -> fieldName,
                                fieldName -> analyzer.getAnalyzer(fieldName, null)
                                        .analyze(conditions.get(fieldName)).stream()
                .map(Token::term)
                .collect(Collectors.toList())));
        matchDocs.forEach(doc -> results.add(scoreCalculator.calculateScores(indexReader.buildContext(doc,fieldsTokensQuery))));
        return results;
    }
}
