package org.storage.invertedIndex;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions.FieldNotFoundException;
import org.utils.Exceptions.InvalidIndexEntryException;
import org.utils.Exceptions.TermNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class InMemoryInvertedIndex implements InvertedIndex {
    @Getter
    private Map<String, Map<String, PostingList>> index = new HashMap<>();

    public void addField(String fieldName, String token, String docId, int position, int docLength) {
        log.debug("Adding field to inverted index: field {}, token {}, docId {}, position {}", fieldName, token, docId, position);
        addTerm(fieldName, token, docId, position, docLength);
    }

    public void addTerm(String fieldName, String token, String docId, int position, int docLength) {
        validateIndexEntry(fieldName, token, docId, position, docLength);
        if (!index.containsKey(fieldName)) {
            log.debug("Creating new field entry in inverted index: field {}", fieldName);
            index.put(fieldName, new HashMap<>());
        }
        Map<String, PostingList> fieldEntry = index.get(fieldName);
        if (!fieldEntry.containsKey(token)) {
            log.debug("Creating new posting list: field {}, token {}, docId {}", fieldName, token, docId);
            Map<String, TermStats> postings = new HashMap<>();
            postings.put(docId, new TermStats(1.0 / docLength, new ArrayList<>(List.of(position))));
            fieldEntry.put(token, new PostingList(postings));
        } else {
            log.debug("Updating posting list: field {}, token {}, docId {}", fieldName, token, docId);
            addDoc(fieldEntry, token, docId, position, docLength);
        }

    }

    private void addDoc(Map<String, PostingList> fieldEntry, String token, String docId, int position, int docLength) {
        Map<String, TermStats> tokenEntry = fieldEntry.get(token).getPostings();
        if (tokenEntry.containsKey(docId)) {
            log.debug("Updating term stats for existing document: token {}, docId {}, position {}", token, docId, position);
            tokenEntry.get(docId).incrementTf(1.0 / docLength);
            tokenEntry.get(docId).getPositions().add(position);
        } else {
            log.debug("Adding document to posting list: token {}, docId {}, position {}", token, docId, position);
            tokenEntry.put(docId, new TermStats(1.0 / docLength, new ArrayList<>(List.of(position))));
        }
    }
    public PostingList getPostingListByTerm(String fieldName, String term){
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
    public Map<String,PostingList> getPostings(String fieldName){
        validateFieldName(fieldName);
        Map<String, PostingList> postings = index.get(fieldName);
        if (postings == null) {
            log.warn("Postings lookup failed because field was not found: field {}", fieldName);
            throw new FieldNotFoundException("Field not found in inverted index: " + fieldName);
        }
        return postings; }

    private void validateIndexEntry(String fieldName, String token, String docId, int position, int docLength) {
        validateFieldName(fieldName);
        validateTerm(token);
        if (docId == null || docId.isBlank()) {
            throw new InvalidIndexEntryException("Document id cannot be null or blank");
        }
        if (position < 0) {
            throw new InvalidIndexEntryException("Position cannot be negative: " + position);
        }
        if (docLength <= 0) {
            throw new InvalidIndexEntryException("Document length must be positive: " + docLength);
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
