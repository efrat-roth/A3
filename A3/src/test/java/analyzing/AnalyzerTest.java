package analyzing;

import org.analyzing.Analyzer;
import org.analyzing.Token;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AnalyzerTest {

    @Test
    void analyzeShouldApplyCharFiltersBeforeTokenizing() throws IOException {
        AtomicReference<String> tokenizerInput = new AtomicReference<>();
        Analyzer analyzer = Analyzer.builder()
                .charFilters(List.of(
                        input -> input + " world",
                        String::toUpperCase
                ))
                .tokenizer(input -> {
                    tokenizerInput.set(input);
                    return List.of(new Token("hello", 1));
                })
                .tokenFilters(List.of(tokens -> tokens))
                .build();

        List<Token> result = analyzer.analyze("hello");

        assertThat(tokenizerInput.get()).isEqualTo("HELLO WORLD");
        assertThat(result).containsExactly(new Token("hello", 1));
    }

    @Test
    void analyzeShouldApplyTokenFiltersAfterTokenizing() throws IOException {
        Analyzer analyzer = Analyzer.builder()
                .charFilters(List.of(input -> input))
                .tokenizer(input -> List.of(
                        new Token("the", 1),
                        new Token("quick", 2),
                        new Token("brown", 3)
                ))
                .tokenFilters(List.of(
                        tokens -> tokens.stream()
                                .filter(token -> !"the".equals(token.term()))
                                .toList(),
                        tokens -> tokens.stream()
                                .map(token -> new Token(token.term().toUpperCase(), token.position()))
                                .toList()
                ))
                .build();

        List<Token> result = analyzer.analyze("ignored input");

        assertThat(result).containsExactly(
                new Token("QUICK", 2),
                new Token("BROWN", 3)
        );
    }

    @Test
    void analyzeShouldWorkWhenNoFiltersAreConfigured() throws IOException {
        Analyzer analyzer = Analyzer.builder()
                .charFilters(List.of())
                .tokenizer(input -> List.of(new Token(input, 1)))
                .tokenFilters(List.of())
                .build();

        List<Token> result = analyzer.analyze("unchanged");

        assertThat(result).containsExactly(new Token("unchanged", 1));
    }

    @Test
    void analyzeShouldPropagateIOExceptionFromTokenFilter() {
        Analyzer analyzer = Analyzer.builder()
                .charFilters(List.of(input -> input))
                .tokenizer(input -> List.of(new Token(input, 1)))
                .tokenFilters(List.of(tokens -> {
                    throw new IOException("token filter failed");
                }))
                .build();

        assertThatThrownBy(() -> analyzer.analyze("hello"))
                .isInstanceOf(IOException.class)
                .hasMessage("token filter failed");
    }
}
