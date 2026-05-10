package org.storage;

import lombok.Data;
import org.analyzing.Token;

import java.util.ArrayList;
import java.util.List;

@Data
public class FieldValue {
    private final String content;
    private List<Token> values = new ArrayList<>();
}
