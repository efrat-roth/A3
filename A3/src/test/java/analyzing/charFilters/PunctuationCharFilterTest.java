package analyzing.charFilters;

import org.analyzing.charFilters.PunctuationCharFilter;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PunctuationCharFilterTest {

    @Test
    void applyShouldRemoveConfiguredPunctuationCharacters() {
        PunctuationCharFilter filter = new PunctuationCharFilter(Set.of(',', '!', '.'));

        String result = filter.apply("Hello, world! Testing.");

        assertThat(result).isEqualTo("Hello world Testing");
    }

    @Test
    void applyShouldKeepCharactersThatAreNotConfiguredAsPunctuation() {
        PunctuationCharFilter filter = new PunctuationCharFilter(Set.of(','));

        String result = filter.apply("a,b.c!");

        assertThat(result).isEqualTo("ab.c!");
    }

    @Test
    void applyShouldReturnEmptyStringWhenInputIsEmpty() {
        PunctuationCharFilter filter = new PunctuationCharFilter(Set.of(',', '!'));

        String result = filter.apply("");

        assertThat(result).isEmpty();
    }

    @Test
    void applyShouldNotChangeInputWhenNoPunctuationIsConfigured() {
        PunctuationCharFilter filter = new PunctuationCharFilter();

        String result = filter.apply("Hello, world!");

        assertThat(result).isEqualTo("Hello, world!");
    }
}
