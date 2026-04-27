package indexing;

import analyzing.analyzerStrategy.AnalyzerStrategy;
import storage.Field;
import storage.IndexStorage;

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
                analyzerStrategy.getAnalyzer(field.getFieldName(), document)
                        .analyze(field.getContent()).forEach(term -> indexStorage.getInvertedIndex()
                                .addField(field.getFieldName(), term.term(), docId, term.position(), docLength)));
        indexStorage.addDocument(docId, document);
    }
}
