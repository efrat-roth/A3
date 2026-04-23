package storage;

import  lombok.Data;
import lombok.NonNull;

import java.lang.reflect.Type;
import java.util.List;
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
    //term and position
    private final Map<String, Integer> values;
}
