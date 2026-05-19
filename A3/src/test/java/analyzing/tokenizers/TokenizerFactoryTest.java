package analyzing.tokenizers;

import org.analyzing.tokenizers.Tokenizer;
import org.analyzing.tokenizers.TokenizerFactory;
import org.analyzing.tokenizers.WhitespaceTokenizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TokenizerFactoryTest {

    @Test
    void getShouldReturnWhitespaceTokenizerForWhitespaceName() {
        TokenizerFactory factory = new TokenizerFactory();

        Tokenizer tokenizer = factory.get("whitespace");

        assertThat(tokenizer).isInstanceOf(WhitespaceTokenizer.class);
    }

    @Test
    void getShouldReturnNewTokenizerInstanceEachTime() {
        TokenizerFactory factory = new TokenizerFactory();

        Tokenizer firstTokenizer = factory.get("whitespace");
        Tokenizer secondTokenizer = factory.get("whitespace");

        assertThat(firstTokenizer).isNotSameAs(secondTokenizer);
    }

    @Test
    void getShouldThrowNullPointerExceptionForUnknownTokenizerName() {
        TokenizerFactory factory = new TokenizerFactory();

        assertThatThrownBy(() -> factory.get("unknown"))
                .isInstanceOf(NullPointerException.class);
    }
}
