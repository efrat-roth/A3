package storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import storage.InvertedIndex.InvertedIndex;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class IndexStorage {
    @Getter
    private final Map<String, HashMap<Field, String>> documents = new HashMap<>();
    @Getter
    private final InvertedIndex invertedIndex;

    public void addDocument(String documentId, HashMap<Field, String> document) {
        if (document == null) {
            log.warn("Document has not been initialized: null pointer");
            return;
        } else if (this.documents.containsKey(documentId)) {
            log.warn("Document has already been initialized: id {}", documentId);
        }
        int docLength = document.keySet().stream().map(Field::getLength).mapToInt(Integer::intValue).sum();
        //add to document
        documents.put(documentId, new HashMap<>(document.entrySet().stream()
                .filter(item -> item.getKey().isStored())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
        //add each term to the invertedIndex
        Stream<Map.Entry<Field, String>> indexedFields = document.entrySet().stream()
                .filter(field -> field.getKey().isIndexed());
        indexedFields.forEach(field -> field.getKey().getValues().forEach((key, value1) ->
                invertedIndex.addField(field.getKey().getFieldName(), key, documentId, value1, docLength)));

    }

    public HashMap<Field, String> getDocument(String documentId) {
        if (this.documents.containsKey(documentId)) {
            return this.documents.get(documentId);
        }
        log.warn("Document not found: id {}", documentId);
        //add exception
        return null;
    }
}
