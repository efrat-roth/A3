package org.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.storage.invertedIndex.InvertedIndex;
import org.utils.Exceptions.DocumentNotFoundException;
import org.utils.Exceptions.DuplicateDocumentException;
import org.utils.Exceptions.InvalidDocumentException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class IndexStorage {
    @Getter
    private Map<String, List<FieldType>> documents = new HashMap<>();
    @Getter
    private final InvertedIndex invertedIndex;

    public void addDocument(String documentId, List<FieldType> document) {
        if (documentId == null || documentId.isBlank()) {
            log.warn("Document id is invalid: {}", documentId);
            throw new InvalidDocumentException("Document id cannot be null or blank");
        }
        if (document == null) {
            log.warn("Document has not been initialized: null pointer");
            throw new InvalidDocumentException("Document cannot be null");
        } else if (this.documents.containsKey(documentId)) {
            log.warn("Document has already been initialized: id {}", documentId);
            throw new DuplicateDocumentException("Document already exists: " + documentId);
        } else if (document.stream().anyMatch(Objects::isNull)) {
            log.warn("Document contains null field: id {}", documentId);
            throw new InvalidDocumentException("Document cannot contain null fields: " + documentId);
        }
        int docLength = document.stream().mapToInt(FieldType::getLength).sum();
        log.debug("Adding document to index storage: id {}, fieldCount {}, docLength {}", documentId, document.size(), docLength);
        documents.put(documentId, new ArrayList<>(document.stream()
                .filter(FieldType::isStored)
                .collect(Collectors.toList())) {
        });
        log.info("Document stored: id {}, storedFieldCount {}", documentId, documents.get(documentId).size());


    }

    public List<FieldType> getDocument(String documentId) {
        if (documentId == null || documentId.isBlank()) {
            log.warn("Document id is invalid: {}", documentId);
            throw new InvalidDocumentException("Document id cannot be null or blank");
        }
        if (this.documents.containsKey(documentId)) {
            log.debug("Document found: id {}", documentId);
            return this.documents.get(documentId);
        }
        log.warn("Document not found: id {}", documentId);
        throw new DocumentNotFoundException("Document not found: " + documentId);
    }
}
