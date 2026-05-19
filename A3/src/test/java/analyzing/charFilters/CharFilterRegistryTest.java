package analyzing.charFilters;

import org.analyzing.charFilters.CharFilter;
import org.analyzing.charFilters.CharFilterRegistry;
import org.analyzing.charFilters.LowercaseCharFilter;
import org.analyzing.charFilters.PunctuationCharFilter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CharFilterRegistryTest {

    @Test
    void getShouldReturnLowercaseCharFilterForLowercaseName() {
        CharFilterRegistry registry = new CharFilterRegistry();

        CharFilter filter = registry.get("lowercase");

        assertThat(filter).isInstanceOf(LowercaseCharFilter.class);
        assertThat(filter.apply("HELLO")).isEqualTo("hello");
    }

    @Test
    void getShouldReturnPunctuationCharFilterForPunctuationName() {
        CharFilterRegistry registry = new CharFilterRegistry();

        CharFilter filter = registry.get("punctuation");

        assertThat(filter).isInstanceOf(PunctuationCharFilter.class);
        assertThat(filter.apply("Hello, world!")).isEqualTo("Hello, world!");
    }

    @Test
    void getShouldReturnNewFilterInstanceEachTime() {
        CharFilterRegistry registry = new CharFilterRegistry();

        CharFilter firstFilter = registry.get("lowercase");
        CharFilter secondFilter = registry.get("lowercase");

        assertThat(firstFilter).isNotSameAs(secondFilter);
    }

    @Test
    void getShouldThrowNullPointerExceptionForUnknownFilterName() {
        CharFilterRegistry registry = new CharFilterRegistry();

        assertThatThrownBy(() -> registry.get("unknown"))
                .isInstanceOf(NullPointerException.class);
    }
}
