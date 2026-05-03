package org.indexing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Token;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.storage.Field;
import org.storage.IndexStorage;
import org.utils.Exceptions;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class InMemoryIndexWriter implements IndexWriter {

    private final AnalyzerStrategy analyzerStrategy;
    private final IndexStorage indexStorage;

    @Override
    public void addDocument(List<Field> document) {
        validateDocument(document);
        String docId = UUID.randomUUID().toString();
        int docLength = document.stream().mapToInt(Field::getLength).sum();

        log.info("Adding document to in-memory index: docId {}, fieldCount {}, docLength {}",
                docId, document.size(), docLength);

        try {
            List<Field> analyzedFields = analyzeDocument(document, docId);
            writeToIndex(analyzedFields, docId, docLength);
            indexStorage.addDocument(docId, document);
            log.info("Document added to in-memory index: docId {}", docId);

        } catch (IOException e) {
            log.error("Failed indexing document: docId {}", docId, e);

            throw new Exceptions.IndexingException("Failed to index document: " + docId, e);
        }
    }

    private List<Field> analyzeDocument(List<Field> document, String docId) throws IOException {
        List<Field> analyzed = new ArrayList<>();
        for (Field field : document) {
            if (!field.isIndexed()) {
                continue;
            }
            log.debug("Analyzing field: docId {}, field {}", docId, field.getFieldName());

            List<Token> tokens = analyzerStrategy.getAnalyzer(field.getFieldName()).analyze(field.getContent());
            field.setValues(tokens);
            analyzed.add(field);
            log.debug("Field analyzed: docId {}, field {}, tokenCount {}", docId, field.getFieldName(), tokens.size());
        }
        return analyzed;
    }

    private void writeToIndex(List<Field> analyzedFields, String docId, int docLength) {
        analyzedFields.forEach(field -> {
            field.getValues().forEach(token ->
                    indexStorage.getInvertedIndex().addField(
                            field.getFieldName(), token.term(), docId, token.position(), docLength));
        });
    }

    private void validateDocument(List<Field> document) {
        if (document == null) {
            throw new Exceptions.InvalidDocumentException("Document cannot be null");
        }

        if (document.isEmpty()) {
            throw new Exceptions.InvalidDocumentException("Document cannot be empty");
        }

        boolean hasInvalidField = document.stream().anyMatch(field ->
                field == null || field.getFieldName() == null || field.getFieldName().isBlank());

        if (hasInvalidField) {
            throw new Exceptions.InvalidFieldException("Document contains invalid field");
        }
    }
}