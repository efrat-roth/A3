package org.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.analyzing.Token;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class FieldValue {
    @NonNull
    private final FieldDefinition definition;
    @NonNull
    private final String content;
    private final int length;
    private final List<Token> tokens = new ArrayList<>();

    public void addToken(Token token) {
        tokens.add(token);
    }
}