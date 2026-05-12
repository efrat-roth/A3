package org.indexing;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.storage.FieldDefinition;
import org.storage.FieldValue;
import org.utils.Exceptions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.file.Files.readAllLines;

@Slf4j
@AllArgsConstructor
public class IndexFile {

    private final IndexWriter indexWriter;

    public void indexFile(String path) {
        validatePath(path);
        log.info("Starting file indexing: path={}", path);
        try {
            List<FieldValue> document = createDocument(path);

            log.debug("Document created from file: path={}, fieldCount={}", path, document.size());

            indexWriter.addDocument(document);

            log.info("File indexed successfully: path={}", path);

        } catch (IOException e) {
            log.error("Failed reading file for indexing: path={}", path, e);
            throw new Exceptions.InvalidDocumentException("Failed reading file: " + path);

        } catch (RuntimeException e) {
            log.error("Failed indexing file: path={}", path, e);
            throw e;
        }
    }

    private List<FieldValue> createDocument(String path) throws IOException {
        log.debug("Creating document from file: path={}", path);

        List<String> lines = readAllLines(Path.of(path));
        if (lines.isEmpty()) {
            throw new Exceptions.InvalidDocumentException("Document file is empty: " + path);
        }
        AtomicInteger i = new AtomicInteger(0);
        List<FieldValue> document = lines.stream().map(line -> buildField(line, i.getAndIncrement())).toList();

        log.debug("Document creation completed: fieldCount={}", document.size());
        return document;
    }

    private FieldValue buildField(String line, int lineNumber) {
        log.debug("Building field from line {}", lineNumber);

        String[] fieldSplit = line.split(":", 2);
        String fieldName;
        String content;
        if (fieldSplit.length < 2) {
            log.warn("Field without explicit name at line {}", lineNumber);

            fieldName = "undefined";
            content = fieldSplit[0].trim();
        } else {
            fieldName = fieldSplit[0].trim();
            content = fieldSplit[1].trim();
        }
        if (content.isBlank()) {
            throw new Exceptions.InvalidFieldException("Empty content for field '" + fieldName + "' at line " + lineNumber);
        }

        log.debug("Field built: name={}, length={}", fieldName, content.length());
        FieldDefinition definition = indexWriter.getOrCreateFieldDefinition(
                fieldName,
                true,
                true
        );

        return new FieldValue(definition, content, content.length());
    }

    private void validatePath(String path) {
        if (path == null || path.isBlank()) {
            throw new Exceptions.InvalidDocumentException("File path cannot be null or blank");
        }
    }
}