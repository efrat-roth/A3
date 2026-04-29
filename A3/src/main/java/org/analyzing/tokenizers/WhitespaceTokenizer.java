package org.analyzing.tokenizers;

import org.analyzing.Token;

import java.util.ArrayList;
import java.util.List;

public class WhitespaceTokenizer implements Tokenizer {
    @Override
    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int position = 1;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == ' ') {
                if (!sb.isEmpty()) {
                    tokens.add(new Token(sb.toString(), position++));
                    sb.setLength(0);
                }
            } else {
                sb.append(c);
            }
        }

        if (!sb.isEmpty()) {
            tokens.add(new Token(sb.toString(), position));
        }

        return tokens;
    }
}
