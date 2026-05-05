package org.indexing;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.storage.Field;
import org.utils.Exceptions;
import org.utils.FileReader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class IndexFile {

    private final IndexWriter indexWriter;

    public void indexFile(String path) {
        validatePath(path);
        log.info("Starting file indexing: path={}", path);
        try {
            List<Field> document = createDocument(path);

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

    private List<Field> createDocument(String path) throws IOException {
        log.debug("Creating document from file: path={}", path);

        List<String> lines = FileReader.readFileLines(path);
        if (lines.isEmpty()) {
            throw new Exceptions.InvalidDocumentException("Document file is empty: " + path);
        }
        List<Field> document = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            log.debug("Processing line {}: {}", i + 1, line);

            document.add(buildField(line, i + 1));
        }

        log.debug("Document creation completed: fieldCount={}", document.size());
        return document;
    }

    private Field buildField(String line, int lineNumber) {
        if (line == null || line.isBlank()) {
            throw new Exceptions.InvalidFieldException("Empty field at line " + lineNumber);
        }

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
        Type type = typeDefiner(content);

        log.debug("Field built: name={}, type={}, length={}", fieldName, type.getTypeName(), content.length());

        return new Field(fieldName, type, content.length(), true, true, content);
    }

    private Type typeDefiner(String content) {

        log.debug("Defining type for content: {}", content);

        if (content == null || content.isBlank()) {
            throw new Exceptions.InvalidFieldException("Field content cannot be null or blank");
        }
        if (content.startsWith("\"") && content.endsWith("\"")) {
            return String.class;
        }
        if (content.startsWith("[") && content.endsWith("]")) {
            return List.class;
        }
        if (content.startsWith("{") && content.endsWith("}")) {
            return Map.class;
        }
        if (content.equals("true") || content.equals("false")) {
            return Boolean.class;
        }
        try {
            Integer.parseInt(content);
            return Integer.class;

        } catch (NumberFormatException ignored) {
        }
        try {
            Long.parseLong(content);
            return Long.class;
        } catch (NumberFormatException ignored) {
        }

        try {
            Double.parseDouble(content);
            return Double.class;

        } catch (NumberFormatException ignored) {
        }

        log.error("Unsupported field type detected: {}", content);

        throw new Exceptions.UnsupprtedFieldTypeException("Unsupported field type: " + content);
    }

    private void validatePath(String path) {
        if (path == null || path.isBlank()) {
            throw new Exceptions.InvalidDocumentException("File path cannot be null or blank");
        }
    }
}