package analyzing.charFilters;

import org.analyzing.charFilters.LowercaseCharFilter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LowercaseCharFilterTest {

    @Test
    void applyShouldConvertUppercaseLettersToLowercase() {
        LowercaseCharFilter filter = new LowercaseCharFilter();

        String result = filter.apply("HELLO WORLD");

        assertThat(result).isEqualTo("hello world");
    }

    @Test
    void applyShouldKeepAlreadyLowercaseTextUnchanged() {
        LowercaseCharFilter filter = new LowercaseCharFilter();

        String result = filter.apply("already lowercase");

        assertThat(result).isEqualTo("already lowercase");
    }

    @Test
    void applyShouldKeepDigitsAndPunctuationUnchanged() {
        LowercaseCharFilter filter = new LowercaseCharFilter();

        String result = filter.apply("A1, B2!");

        assertThat(result).isEqualTo("a1, b2!");
    }

    @Test
    void applyShouldReturnEmptyStringWhenInputIsEmpty() {
        LowercaseCharFilter filter = new LowercaseCharFilter();

        String result = filter.apply("");

        assertThat(result).isEmpty();
    }
}
