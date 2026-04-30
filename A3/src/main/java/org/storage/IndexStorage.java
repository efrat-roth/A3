package org.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.storage.invertedIndex.InvertedIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class IndexStorage {
    @Getter
    private Map<String, List<Field>> documents = new HashMap<>();
    @Getter
    private final InvertedIndex invertedIndex;

    public void addDocument(String documentId, List<Field> document) {
        if (document == null) {
            log.warn("Document has not been initialized: null pointer");
            return;
        } else if (this.documents.containsKey(documentId)) {
            log.warn("Document has already been initialized: id {}", documentId);
        }
        int docLength = document.stream().mapToInt(Field::getLength).sum();
        log.debug("Adding document to index storage: id {}, fieldCount {}, docLength {}", documentId, document.size(), docLength);
        documents.put(documentId, new ArrayList<>(document.stream()
                .filter(Field::isStored)
                .collect(Collectors.toList())) {
        });
        log.info("Document stored: id {}, storedFieldCount {}", documentId, documents.get(documentId).size());


    }

    public List<Field> getDocument(String documentId) {
        if (this.documents.containsKey(documentId)) {
            log.debug("Document found: id {}", documentId);
            return this.documents.get(documentId);
        }
        log.warn("Document not found: id {}", documentId);
        //add exception
        return null;
    }
}
