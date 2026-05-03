package org.analyzing.tokenizers;

import lombok.extern.slf4j.Slf4j;
import org.analyzing.Token;
import org.utils.Exceptions;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WhitespaceTokenizer implements Tokenizer {

    @Override
    public List<Token> tokenize(String input) {

        if (input == null) {
            throw new Exceptions.InvalidDocumentException("Tokenizer input cannot be null");
        }

        log.debug("Tokenizing input with whitespace tokenizer: inputLength {}", input.length());

        List<Token> tokens = new ArrayList<>();
        int position = 1;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!sb.isEmpty()) {
                    tokens.add(new Token(sb.toString(), position++));
                    sb.setLength(0);
                }
            } else {
                sb.append(c);
            }
        }
        if (!sb.isEmpty()) {
            tokens.add( new Token(sb.toString(), position));
        }
        log.debug("Whitespace tokenization completed: tokenCount {}", tokens.size());
        return tokens;
    }
}