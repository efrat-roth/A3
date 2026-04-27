package analyzing.tokenizers;

import analyzing.Token;

import java.util.List;

public interface Tokenizer {
    List<Token> tokenize(String input);
}
