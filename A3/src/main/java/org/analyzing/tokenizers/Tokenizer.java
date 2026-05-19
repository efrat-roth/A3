package org.analyzing.tokenizers;

import org.analyzing.Token;

import java.util.List;

public interface Tokenizer {
    List<Token> tokenize(String input);
}
