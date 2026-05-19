package quering;

import org.junit.jupiter.api.Test;
import org.quering.QueryType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QueryTypeTest {

    @Test
    void getValueShouldReturnConfiguredValue() {
        assertThat(QueryType.AND.getValue()).isEqualTo("and");
        assertThat(QueryType.RANGE.getValue()).isEqualTo("range");
    }

    @Test
    void fromValueShouldReturnMatchingQueryTypeIgnoringCase() {
        assertThat(QueryType.fromValue("and")).isEqualTo(QueryType.AND);
        assertThat(QueryType.fromValue("OR")).isEqualTo(QueryType.OR);
    }

    @Test
    void fromValueShouldThrowIllegalArgumentExceptionForUnknownValue() {
        assertThatThrownBy(() -> QueryType.fromValue("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown query type: unknown");
    }
}
