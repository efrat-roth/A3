package analyzing.tokenFilters;

import org.analyzing.Token;
import org.analyzing.tokenFilters.StopwordsTokenFilter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StopwordsTokenFilterTest {

    @Test
    void applyShouldRemoveTokensWhoseTermsAreStopwords() throws IOException {
        StopwordsTokenFilter filter = new StopwordsTokenFilter(Set.of("the", "and"));
        List<Token> tokens = List.of(
                new Token("the", 0),
                new Token("quick", 1),
                new Token("and", 2),
                new Token("brown", 3)
        );

        List<Token> result = filter.apply(tokens);

        assertThat(result).containsExactly(
                new Token("quick", 1),
                new Token("brown", 3)
        );
    }

    @Test
    void applyShouldPreserveTokenOrderAndPositions() throws IOException {
        StopwordsTokenFilter filter = new StopwordsTokenFilter(Set.of("is"));
        List<Token> tokens = List.of(
                new Token("java", 4),
                new Token("is", 5),
                new Token("fast", 6)
        );

        List<Token> result = filter.apply(tokens);

        assertThat(result).containsExactly(
                new Token("java", 4),
                new Token("fast", 6)
        );
    }

    @Test
    void applyShouldReturnAllTokensWhenNoTermsAreStopwords() throws IOException {
        StopwordsTokenFilter filter = new StopwordsTokenFilter(Set.of("missing"));
        List<Token> tokens = List.of(
                new Token("hello", 0),
                new Token("world", 1)
        );

        List<Token> result = filter.apply(tokens);

        assertThat(result).containsExactlyElementsOf(tokens);
    }

    @Test
    void applyShouldReturnEmptyListWhenAllTokensAreStopwords() throws IOException {
        StopwordsTokenFilter filter = new StopwordsTokenFilter(Set.of("a", "the"));

        List<Token> result = filter.apply(List.of(
                new Token("a", 0),
                new Token("the", 1)
        ));

        assertThat(result).isEmpty();
    }

    @Test
    void applyShouldPropagateIOExceptionWhenStopwordsCannotBeRead() {
        StopwordsTokenFilter filter = new StopwordsTokenFilter();

        assertThatThrownBy(() -> filter.apply(List.of(new Token("hello", 0))))
                .isInstanceOf(IOException.class);
    }
}
