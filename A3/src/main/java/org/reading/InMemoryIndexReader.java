package org.reading;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.storage.IndexStorage;
import org.storage.invertedIndex.PostingList;
import org.storage.invertedIndex.TermStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class InMemoryIndexReader implements IndexReader {
    private final IndexStorage indexStorage;

    @Override
    public Map<String, PostingList> getPosting(String fieldName) {
        log.debug("Retrieving postings for field: {}", fieldName);
        Map<String, PostingList> postings = indexStorage.getInvertedIndex().getPostings(fieldName);
        if (postings == null) {
            log.warn("No postings found for field: {}", fieldName);
        } else {
            log.debug("Postings retrieved for field: {}, termCount {}", fieldName, postings.size());
        }
        return postings;
    }

    @Override
    public QueryContext buildContext(String docId, Map<String, List<String>> queryTermsByFields) {
        log.debug("Building query context: docId {}, fieldCount {}", docId, queryTermsByFields.size());
        List<TermStats> termStatsOfDoc = new ArrayList<>();
        for (String fieldName : queryTermsByFields.keySet()) {
            log.debug("Collecting term stats for field: {}, termCount {}", fieldName, queryTermsByFields.get(fieldName).size());
            for (String term : queryTermsByFields.get(fieldName)) {
                log.debug("Collecting term stats: field {}, term {}, docId {}", fieldName, term, docId);
                termStatsOfDoc.add(indexStorage.getInvertedIndex().getPostingListByTerm(fieldName, term).getPostings().get(docId));
            }
        }
        Map<String, Integer> termAppearanceInIndex = new HashMap<>();
        for (String termsInField : queryTermsByFields.keySet()) {
            for( String term : queryTermsByFields.get(termsInField)) {
                log.debug("Calculating term appearance in index: field {}, term {}", termsInField, term);
                termAppearanceInIndex.put(term,
                        indexStorage.getInvertedIndex().getPostingListByTerm(termsInField, term).getPostings().values()
                                .stream().mapToInt(termStats -> termStats.getPositions().size()).sum());
            }
        }
        int totalDocs = indexStorage.getDocuments().size();

        log.info("Query context built: docId {}, termStatsCount {}, termsDfCount {}, totalDocs {}",
                docId, termStatsOfDoc.size(), termAppearanceInIndex.size(), totalDocs);
        return new QueryContext(docId ,termStatsOfDoc, termAppearanceInIndex, totalDocs);
    }

}
