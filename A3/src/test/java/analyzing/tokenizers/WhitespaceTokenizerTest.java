package analyzing.tokenizers;

import org.analyzing.Token;
import org.analyzing.tokenizers.WhitespaceTokenizer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WhitespaceTokenizerTest {

    @Test
    void tokenizeShouldSplitInputBySpaces() {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();

        List<Token> tokens = tokenizer.tokenize("hello world java");

        assertThat(tokens).containsExactly(
                new Token("hello", 1),
                new Token("world", 2),
                new Token("java", 3)
        );
    }

    @Test
    void tokenizeShouldIgnoreLeadingTrailingAndRepeatedSpaces() {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();

        List<Token> tokens = tokenizer.tokenize("  hello   world  ");

        assertThat(tokens).containsExactly(
                new Token("hello", 1),
                new Token("world", 2)
        );
    }

    @Test
    void tokenizeShouldReturnSingleTokenWhenInputHasNoSpaces() {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();

        List<Token> tokens = tokenizer.tokenize("hello");

        assertThat(tokens).containsExactly(new Token("hello", 1));
    }

    @Test
    void tokenizeShouldReturnEmptyListForEmptyInput() {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();

        List<Token> tokens = tokenizer.tokenize("");

        assertThat(tokens).isEmpty();
    }

    @Test
    void tokenizeShouldSplitOnlyOnSpaceCharacter() {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();

        List<Token> tokens = tokenizer.tokenize("hello\tworld\njava");

        assertThat(tokens).containsExactly(new Token("hello\tworld\njava", 1));
    }
}
