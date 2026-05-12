package org.reading;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.storage.FieldType;
import org.storage.IndexStorage;
import org.storage.invertedIndex.InvertedIndex;
import org.storage.invertedIndex.Posting;
import org.storage.invertedIndex.PostingList;
import org.storage.invertedIndex.TermStats;
import org.utils.Exceptions;

import java.util.*;

@Slf4j
@AllArgsConstructor
public class InMemoryIndexReader implements IndexReader {

    private final IndexStorage indexStorage;

    @Override
    public Optional<Map<String, PostingList>> getPosting(@NonNull String fieldName) {
        log.debug("Retrieving postings for field: {}", fieldName);
        Optional<Map<String, PostingList>> postings = Optional.of(indexStorage.getInvertedIndex().getPostings(fieldName));
        log.debug("Postings retrieved for field: {}, termCount {}", fieldName, postings.get().size());
        return postings;
    }

    @Override
    public QueryContext buildContext(String docId, Map<String, List<String>> queryTermsByFields) {
        validateBuildContextInput(queryTermsByFields);

        log.debug("Building query context: docId {}, fieldCount {}", docId, queryTermsByFields.size());

        List<TermScoreDTO> termScores = new ArrayList<>();
        int totalDocs = indexStorage.getDocuments().size();

        for (Map.Entry<String, List<String>> fieldEntry : queryTermsByFields.entrySet()) {
            String fieldName = fieldEntry.getKey();

            for (String term : fieldEntry.getValue()) {
                InvertedIndex inverted = indexStorage.getInvertedIndex();
                PostingList postingList;
                try {
                    postingList = inverted.getPostingListByTerm(fieldName, term);
                } catch (Exceptions.TermNotFoundException e) {
                    log.debug("Skipping missing term: field {}, term {}", fieldName, term);
                    continue;
                }

                int df = postingList.getPostings().size();
                double idf = Math.log((totalDocs + 1.0) / (df + 1.0));
                Posting posting = postingList.getPostings().get(docId);

                if (posting != null) {
                    termScores.add(new TermScoreDTO(term, posting, idf));
                }


            }
        }
        log.info("Query context built: docId {}, termStatsCount {}", docId, termScores);

        return new QueryContext(docId, termScores);
    }

    @Override
    public List<FieldType> getDocument(String docId) {
        return indexStorage.getDocument(docId);
    }

    private void validateBuildContextInput(Map<String, List<String>> queryTermsByFields) {

        if (queryTermsByFields == null || queryTermsByFields.isEmpty()) {
            throw new Exceptions.InvalidIndexEntryException("Query terms cannot be null or empty");
        }
    }
}