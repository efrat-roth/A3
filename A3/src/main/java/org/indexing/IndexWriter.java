package org.indexing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Token;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.storage.FieldDefinition;
import org.storage.FieldValue;
import org.storage.IndexStorage;
import org.utils.Exceptions;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public abstract class IndexWriter {

    private final AnalyzerStrategy analyzerStrategy;
    private final IndexStorage indexStorage;
    private final Map<String, FieldDefinition> schema = new HashMap<>();

    public FieldDefinition getOrCreateFieldDefinition(String fieldName, boolean stored, boolean indexed) {
        return schema.computeIfAbsent(fieldName, f ->
                new FieldDefinition(fieldName, stored, indexed)
        );
    }

    public void addDocument(List<FieldValue> document) {
        validateDocument(document);

        String docId = UUID.randomUUID().toString();

        log.info("Adding document to index: docId {}, fieldCount {}",
                docId, document.size());

        try {
            List<FieldValue> analyzedFields = analyzeDocument(document, docId);
            writeToIndex(analyzedFields, docId);
            indexStorage.addDocument(docId, document);

            log.info("Document added successfully: docId {}", docId);

        } catch (IOException e) {
            log.error("Failed indexing document: docId {}", docId, e);
            throw new Exceptions.IndexingException("Failed to index document: " + docId, e);
        }
    }

    private List<FieldValue> analyzeDocument(List<FieldValue> document, String docId) throws IOException {
        List<FieldValue> analyzed = new ArrayList<>();

        for (FieldValue field : document) {

            if (!field.getDefinition().indexed()) {
                continue;
            }

            log.debug("Analyzing field: docId {}, field {}", docId, field.getDefinition().fieldName());

            List<Token> tokens = analyzerStrategy.getAnalyzer(field.getDefinition().fieldName())
                            .analyze(field.getContent());

            tokens.forEach(field::addToken);
            analyzed.add(field);

            log.debug("Field analyzed: docId {}, field {}, tokenCount {}",
                    docId, field.getDefinition().fieldName(), tokens.size());
        }

        return analyzed;
    }

    private void writeToIndex(List<FieldValue> analyzedFields, String docId) {

        analyzedFields.forEach(field -> {
            String fieldName = field.getDefinition().fieldName();
            field.getTokens().forEach(token ->
                    indexStorage.getInvertedIndex().addTerm(fieldName,token.term(),docId,token.position()));});
    }

    private void validateDocument(List<FieldValue> document) {
        if (document == null) {
            throw new Exceptions.InvalidDocumentException("Document cannot be null");
        }

        if (document.isEmpty()) {
            throw new Exceptions.InvalidDocumentException("Document cannot be empty");
        }

        boolean hasInvalidField = document.stream().anyMatch(field ->
                field == null || field.getDefinition().fieldName().isBlank());

        if (hasInvalidField) {
            throw new Exceptions.InvalidFieldException("Document contains invalid field");
        }
    }
}