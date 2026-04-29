package org.storage;

import lombok.Data;
import lombok.NonNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


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
    //term and position
    private Map<String, Integer> values= new HashMap<>();
}
