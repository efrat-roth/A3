package org.reading;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.storage.Field;
import org.storage.IndexStorage;
import org.storage.invertedIndex.InvertedIndex;
import org.storage.invertedIndex.PostingList;
import org.storage.invertedIndex.TermStats;
import org.utils.Exceptions;

import java.util.*;

@Slf4j
@AllArgsConstructor
public class InMemoryIndexReader implements IndexReader {

    private final IndexStorage indexStorage;

    @Override
    public Map<String, PostingList> getPosting(String fieldName) {
        validateFieldName(fieldName);
        log.debug("Retrieving postings for field: {}", fieldName);
        Map<String, PostingList> postings = indexStorage.getInvertedIndex().getPostings(fieldName);
        if (postings == null) {
            log.warn("No postings found for field: {}", fieldName);
            return Collections.emptyMap();
        }
        log.debug("Postings retrieved for field: {}, termCount {}", fieldName, postings.size());
        return postings;
    }

    @Override
    public QueryContext buildContext(String docId, Map<String, List<String>> queryTermsByFields) {
        validateBuildContextInput(docId, queryTermsByFields);

        log.debug("Building query context: docId {}, fieldCount {}", docId, queryTermsByFields.size());

        List<TermStats> termStatsOfDoc = new ArrayList<>();
        Map<String, Integer> termDocumentFrequency = new HashMap<>();

        for (Map.Entry<String, List<String>> fieldEntry : queryTermsByFields.entrySet()) {
            String fieldName = fieldEntry.getKey();
            validateFieldName(fieldName);

            for (String term : fieldEntry.getValue()) {
                InvertedIndex inverted = indexStorage.getInvertedIndex();
                PostingList postings;
                try {
                    postings = inverted.getPostingListByTerm(fieldName, term);
                } catch (Exceptions.TermNotFoundException e) {
                    log.debug("Skipping missing term: field {}, term {}", fieldName, term);
                    continue;
                }
                TermStats stats = postings.getPostings().get(docId);
                if (stats != null) {
                    termStatsOfDoc.add(stats);
                }
                termDocumentFrequency.computeIfAbsent(term, t -> postings.getPostings().size());
            }
        }

        int totalDocs = indexStorage.getDocuments().size();

        if (totalDocs <= 0) {
            throw new Exceptions.InvalidIndexEntryException("Index contains no documents");
        }

        log.info("Query context built: docId {}, termStatsCount {}, termsDfCount {}, totalDocs {}",
                docId, termStatsOfDoc.size(), termDocumentFrequency.size(), totalDocs);

        return new QueryContext(docId, termStatsOfDoc, termDocumentFrequency, totalDocs);
    }

    @Override
    public List<Field> getDocument(String docId) {
        return indexStorage.getDocument(docId);
    }

    private void validateFieldName(String fieldName) {
        if (fieldName == null || fieldName.isBlank()) {
            throw new Exceptions.InvalidFieldException("Field name cannot be null or blank");
        }
    }

    private void validateBuildContextInput(String docId, Map<String, List<String>> queryTermsByFields) {
        if (docId == null || docId.isBlank()) {
            throw new Exceptions.DocumentNotFoundException("Document id cannot be null or blank");
        }

        if (queryTermsByFields == null || queryTermsByFields.isEmpty()) {
            throw new Exceptions.InvalidIndexEntryException("Query terms cannot be null or empty");
        }
    }
}