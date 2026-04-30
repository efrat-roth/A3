package org.indexing;

import lombok.extern.slf4j.Slf4j;
import org.analyzing.Token;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.storage.Field;
import org.storage.IndexStorage;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
public class InMemoryIndexWriter implements IndexWriter {
    private AnalyzerStrategy analyzerStrategy;
    private IndexStorage indexStorage;

    public void addDocument(List<Field> document) {
        String docId = UUID.randomUUID().toString();
        int docLength = document.stream().mapToInt(Field::getLength).sum();
        log.info("Adding document to in-memory index: docId {}, fieldCount {}, docLength {}", docId, document.size(), docLength);
        Stream<Field> indexedFields = document.stream().filter(Field::isIndexed);
        indexedFields.forEach(field ->
        {
            try {
                log.debug("Indexing field: docId {}, field {}", docId, field.getFieldName());
                List<Token> tokens = analyzerStrategy.getAnalyzer(field.getFieldName())
                        .analyze(field.getContent());
                log.debug("Field analyzed for indexing: docId {}, field {}, tokenCount {}",
                        docId, field.getFieldName(), tokens.size());
                tokens.forEach(term -> indexStorage.getInvertedIndex()
                        .addField(field.getFieldName(), term.term(), docId, term.position(), docLength));
                log.debug("Field indexed: docId {}, field {}, tokenCount {}", docId, field.getFieldName(), tokens.size());
            } catch (IOException e) {
                log.error("Failed to analyze field for indexing: docId {}, field {}", docId, field.getFieldName(), e);
                throw new RuntimeException(e);
            }
        });
        indexStorage.addDocument(docId, document);
        log.info("Document added to in-memory index: docId {}", docId);
    }
}
