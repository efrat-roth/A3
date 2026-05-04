package org.storage;

import lombok.Data;
import lombok.NonNull;
import org.analyzing.Token;
import org.utils.Exceptions.InvalidFieldException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@Data
public class Field {
    @NonNull
    private String fieldName;
    @NonNull
    private Type fieldType;
    private final int length;
    private final boolean stored;
    private final boolean indexed;
    private final String content;
    private List<Token> values= new ArrayList<>();

    public Field(String fieldName, Type fieldType, int length, boolean stored, boolean indexed, String content) {
        if (fieldName == null) {
            throw new InvalidFieldException("Field name cannot be null");
        }
        if (fieldName.isBlank()) {
            throw new InvalidFieldException("Field name cannot be blank");
        }
        if (fieldType == null) {
            throw new InvalidFieldException("Field type cannot be null");
        }
        if (length < 0) {
            throw new InvalidFieldException("Field length cannot be negative: " + length);
        }
        if (content == null) {
            throw new InvalidFieldException("Field content cannot be null");
        }
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.length = length;
        this.stored = stored;
        this.indexed = indexed;
        this.content = content;
    }
}
