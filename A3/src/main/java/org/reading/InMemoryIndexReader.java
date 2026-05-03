package org.reading;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.storage.Field;
import org.storage.IndexStorage;
import org.storage.invertedIndex.PostingList;
import org.storage.invertedIndex.TermStats;
import org.utils.Exceptions;

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
        validateFieldName(fieldName);
        log.debug("Retrieving postings for field: {}", fieldName);
        Map<String, PostingList> postings = indexStorage.getInvertedIndex().getPostings(fieldName);
        if (postings == null) {
            log.warn("No postings found for field: {}", fieldName);
            throw new Exceptions.FieldNotFoundException("Field not found in index: " + fieldName);
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
            List<String> terms = fieldEntry.getValue();
            validateFieldName(fieldName);

            for (String term : terms) {
                PostingList postingList = indexStorage.getInvertedIndex().getPostingListByTerm(fieldName, term);
                if (postingList == null) {
                    throw new Exceptions.TermNotFoundException("Term not found: " + term + " in field: " + fieldName);
                }
                TermStats stats = postingList.getPostings().get(docId);
                if (stats == null) {
                    throw new Exceptions.DocumentNotFoundException("Document " + docId + " not found for term: " + term);
                }
                termStatsOfDoc.add(stats);

                termDocumentFrequency.put(term, postingList.getPostings().size());
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