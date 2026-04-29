package org.reading;

import lombok.AllArgsConstructor;
import org.storage.IndexStorage;
import org.storage.invertedIndex.PostingList;
import org.storage.invertedIndex.TermStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class InMemoryIndexReader implements IndexReader {
    private final IndexStorage indexStorage;

    @Override
    public Map<String, PostingList> getPosting(String fieldName) {
        return indexStorage.getInvertedIndex().getPostings(fieldName);
    }

    @Override
    public QueryContext buildContext(String docId, Map<String, List<String>> queryTermsByFields) {
        List<TermStats> termStatsOfDoc = new ArrayList<>();
        for (String fieldName : queryTermsByFields.keySet()) {
            for (String term : queryTermsByFields.get(fieldName)) {
                termStatsOfDoc.add(indexStorage.getInvertedIndex().getPostingListByTerm(fieldName, term).getPostings().get(docId));
            }
        }
        Map<String, Integer> termAppearanceInIndex = new HashMap<>();
        for (String termsInField : queryTermsByFields.keySet()) {
            for( String term : queryTermsByFields.get(termsInField)) {
                termAppearanceInIndex.put(term,
                        indexStorage.getInvertedIndex().getPostingListByTerm(termsInField, term).getPostings().values()
                                .stream().mapToInt(termStats -> termStats.getPositions().size()).sum());
            }
        }
        int totalDocs = indexStorage.getDocuments().size();

        return new QueryContext(docId ,termStatsOfDoc, termAppearanceInIndex, totalDocs);
    }

}
