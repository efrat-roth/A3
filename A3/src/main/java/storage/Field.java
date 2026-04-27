package storage;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.lang.reflect.Type;
import java.util.Map;

@Data
public class Field {
    @NonNull
    private String fieldName;
    @NonNull
    private Type fieldType;
    private int length;
    private boolean stored;
    private boolean indexed;
    private final String content;
    //term and position
    private final Map<String, Integer> values;
}
