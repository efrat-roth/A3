package org.storage;

import lombok.Data;
import lombok.NonNull;
import org.utils.Exceptions.InvalidFieldException;


@Data
public class FieldType {
    @NonNull
    private String fieldName;
    private final int length;
    private final boolean stored;
    private final boolean indexed;

    public FieldType(@NonNull String fieldName, int length, boolean stored, boolean indexed) {
        if (fieldName.isBlank()) {
            throw new InvalidFieldException("Field name cannot be blank");
        }
        if (length < 0) {
            throw new InvalidFieldException("Field length cannot be negative: " + length);
        }
        this.fieldName = fieldName;
        this.length = length;
        this.stored = stored;
        this.indexed = indexed;
    }
}
