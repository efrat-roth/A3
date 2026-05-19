package org.storage.invertedIndex;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions.FieldNotFoundException;
import org.utils.Exceptions.InvalidIndexEntryException;
import org.utils.Exceptions.TermNotFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InMemoryInvertedIndex implements InvertedIndex {
    @Getter
    private Map<String, Map<String, PostingList>> index = new HashMap<>();


    public void addTerm(String fieldName, String token, String docId, int position) {
        validateIndexEntry(fieldName, token, docId, position);
        Map<String, PostingList> fieldEntry = index.computeIfAbsent(fieldName, f -> new HashMap<>());
        PostingList postingList = fieldEntry.computeIfAbsent(token, t -> new PostingList());
        postingList.addOccurrence(docId, position);

        log.debug("Updating posting list: field {}, token {}, docId {}", fieldName, token, docId);

    }

    public PostingList getPostingListByTerm(String fieldName, String term) {
        validateFieldName(fieldName);
        validateTerm(term);
        Map<String, PostingList> fieldEntry = index.get(fieldName);
        if (fieldEntry == null) {
            log.warn("Posting list lookup failed because field was not found: field {}", fieldName);
            throw new FieldNotFoundException("Field not found in inverted index: " + fieldName);
        } else if (!fieldEntry.containsKey(term)) {
            log.warn("Posting list lookup failed because term was not found: field {}, term {}", fieldName, term);
            throw new TermNotFoundException("Term not found in inverted index: field " + fieldName + ", term " + term);
        }
        return fieldEntry.get(term);
    }

    public Map<String, PostingList> getPostings(String fieldName) {
        validateFieldName(fieldName);
        Map<String, PostingList> postings = index.get(fieldName);
        if (postings == null) {
            log.warn("Postings lookup failed because field was not found: field {}", fieldName);
            throw new FieldNotFoundException("Field not found in inverted index: " + fieldName);
        }
        return postings;
    }

    private void validateIndexEntry(String fieldName, String token, String docId, int position) {
        validateFieldName(fieldName);
        validateTerm(token);
        if (docId == null || docId.isBlank()) {
            throw new InvalidIndexEntryException("Document id cannot be null or blank");
        }
        if (position < 0) {
            throw new InvalidIndexEntryException("Position cannot be negative: " + position);
        }
    }

    private void validateFieldName(String fieldName) {
        if (fieldName == null || fieldName.isBlank()) {
            throw new InvalidIndexEntryException("Field name cannot be null or blank");
        }
    }

    private void validateTerm(String term) {
        if (term == null || term.isBlank()) {
            throw new InvalidIndexEntryException("Term cannot be null or blank");
        }
    }
}
