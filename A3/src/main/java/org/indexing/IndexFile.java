package org.indexing;

import org.storage.Field;
import org.utils.Exceptions;
import org.utils.FileReader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexFile {

    public List<Field> indexFile(String path) throws IOException {
        List<String> lines = FileReader.readFileLines(path);
        List<Field> document = new ArrayList<>();
        for (String line : lines) {
            buildField(line);
            document.add(buildField(line));
        }
        return document;
    }

    private Field buildField(String line) {
        String[] fieldSplit = line.split(":", 2);
        String fieldName;
        if (fieldSplit.length < 2) {
            fieldName = "undefined";
        } else {
            fieldName = fieldSplit[0];
        }
        String content = fieldSplit[1].stripLeading().stripTrailing();
        return new Field(fieldName, typeDefiner(content), content.length(), true, true, content);
    }

    private Type typeDefiner(String content) {
        if (content.charAt(0) == '"' && content.charAt(content.length() - 1) == '"') {
            return String.class;
        }
        if (content.charAt(0) == '[' && content.charAt(content.length() - 1) == ']') {
            return List.class;
        }
        if (content.charAt(0) == '{' && content.charAt(content.length() - 1) == '}') {
            return Map.class;
        }
        try {
            Double.parseDouble(content);
            return Integer.class;
        } catch (NumberFormatException e1) {
            try {
                Integer.parseInt(content);
                return Double.class;
            } catch (NumberFormatException e2) {
                throw new Exceptions.UnsupprtedFieldTypeException(content + " hasn't a legal type");
            }
        }
    }
}

