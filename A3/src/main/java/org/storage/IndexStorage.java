package org.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.storage.invertedIndex.InvertedIndex;
import org.utils.Exceptions.DocumentNotFoundException;
import org.utils.Exceptions.DuplicateDocumentException;
import org.utils.Exceptions.InvalidDocumentException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class IndexStorage {

    @Getter
    private final Map<String, List<FieldValue>> documents = new HashMap<>();

    @Getter
    private final InvertedIndex invertedIndex;

    public void addDocument(String documentId, List<FieldValue> document) {

        if (documentId == null || documentId.isBlank()) {
            log.warn("Document id is invalid: {}", documentId);
            throw new InvalidDocumentException("Document id cannot be null or blank");
        }

        if (document == null) {
            log.warn("Document has not been initialized: null pointer");
            throw new InvalidDocumentException("Document cannot be null");
        }

        if (documents.containsKey(documentId)) {
            log.warn("Document already exists: id {}", documentId);
            throw new DuplicateDocumentException("Document already exists: " + documentId);
        }

        if (document.stream().anyMatch(Objects::isNull)) {
            log.warn("Document contains null field: id {}", documentId);
            throw new InvalidDocumentException("Document cannot contain null fields: " + documentId);
        }

        int docLength = document.stream().mapToInt(FieldValue::getLength).sum();

        log.debug("Adding document to index storage: id {}, fieldCount {}, docLength {}",
                documentId, document.size(), docLength);

        List<FieldValue> storedFields = document.stream()
                .filter(f -> f.getDefinition().stored())
                .collect(Collectors.toList());

        documents.put(documentId, storedFields);

        log.info("Document stored: id {}, storedFieldCount {}", documentId, storedFields.size());
    }

    public List<FieldValue> getDocument(String documentId) {

        if (documentId == null || documentId.isBlank()) {
            log.warn("Document id is invalid: {}", documentId);
            throw new InvalidDocumentException("Document id cannot be null or blank");
        }

        List<FieldValue> doc = documents.get(documentId);

        if (doc != null) {
            log.debug("Document found: id {}", documentId);
            return doc;
        }

        log.warn("Document not found: id {}", documentId);
        throw new DocumentNotFoundException("Document not found: " + documentId);
    }
}