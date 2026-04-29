package org.indexing;

import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.storage.Field;
import org.storage.IndexStorage;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class InMemoryIndexWriter implements IndexWriter {
    private AnalyzerStrategy analyzerStrategy;
    private IndexStorage indexStorage;

    public void addDocument(List<Field> document) {
        String docId = UUID.randomUUID().toString();
        int docLength = document.stream().mapToInt(Field::getLength).sum();
        Stream<Field> indexedFields = document.stream().filter(Field::isIndexed);
        indexedFields.forEach(field ->
        {
            try {
                analyzerStrategy.getAnalyzer(field.getFieldName())
                        .analyze(field.getContent()).forEach(term -> indexStorage.getInvertedIndex()
                                .addField(field.getFieldName(), term.term(), docId, term.position(), docLength));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        indexStorage.addDocument(docId, document);
    }
}
