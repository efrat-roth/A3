package analyzing.tokenFilters;

import org.analyzing.tokenFilters.StopwordsTokenFilter;
import org.analyzing.tokenFilters.TokenFilter;
import org.analyzing.tokenFilters.TokenFilterRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TokenFilterRegistryTest {

    @Test
    void getShouldReturnStopwordsTokenFilterForStopwordsName() {
        TokenFilterRegistry registry = new TokenFilterRegistry();

        TokenFilter filter = registry.get("stopwords");

        assertThat(filter).isInstanceOf(StopwordsTokenFilter.class);
    }

    @Test
    void getShouldReturnNewFilterInstanceEachTime() {
        TokenFilterRegistry registry = new TokenFilterRegistry();

        TokenFilter firstFilter = registry.get("stopwords");
        TokenFilter secondFilter = registry.get("stopwords");

        assertThat(firstFilter).isNotSameAs(secondFilter);
    }

    @Test
    void getShouldThrowNullPointerExceptionForUnknownFilterName() {
        TokenFilterRegistry registry = new TokenFilterRegistry();

        assertThatThrownBy(() -> registry.get("unknown"))
                .isInstanceOf(NullPointerException.class);
    }
}
