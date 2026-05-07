package org.storage;

import lombok.Data;
import lombok.NonNull;
import org.analyzing.Token;
import org.utils.Exceptions.InvalidFieldException;

import java.util.ArrayList;
import java.util.List;


@Data
public class Field {
    @NonNull
    private String fieldName;
    private final int length;
    private final boolean stored;
    private final boolean indexed;
    private final String content;
    private List<Token> values = new ArrayList<>();

    public Field(@NonNull String fieldName, int length, boolean stored, boolean indexed, String content) {
        if (fieldName.isBlank()) {
            throw new InvalidFieldException("Field name cannot be blank");
        }
        if (length < 0) {
            throw new InvalidFieldException("Field length cannot be negative: " + length);
        }
        if (content == null) {
            throw new InvalidFieldException("Field content cannot be null");
        }
        this.fieldName = fieldName;
        this.length = length;
        this.stored = stored;
        this.indexed = indexed;
        this.content = content;
    }
}
