package analyzing.analyzerStrategy;

import org.analyzing.Analyzer;
import org.analyzing.analyzerStrategy.FieldBasedAnalyzerStrategy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FieldBasedAnalyzerStrategyTest {

    @Test
    void getAnalyzerShouldReturnAnalyzerConfiguredForRequestedField() throws Exception {
        FieldBasedAnalyzerStrategy strategy = new FieldBasedAnalyzerStrategy();
        Analyzer titleAnalyzer = Analyzer.builder().build();
        Analyzer bodyAnalyzer = Analyzer.builder().build();
        setPrivateField(strategy, "analyzers", Map.of(
                "title", titleAnalyzer,
                "body", bodyAnalyzer
        ));

        assertThat(strategy.getAnalyzer("title")).isSameAs(titleAnalyzer);
        assertThat(strategy.getAnalyzer("body")).isSameAs(bodyAnalyzer);
    }

    @Test
    void getAnalyzerShouldReturnNullWhenFieldIsNotConfigured() throws Exception {
        FieldBasedAnalyzerStrategy strategy = new FieldBasedAnalyzerStrategy();
        setPrivateField(strategy, "analyzers", Map.of("title", Analyzer.builder().build()));

        assertThat(strategy.getAnalyzer("missing")).isNull();
    }

    @Test
    void getAnalyzerShouldThrowNullPointerExceptionWhenAnalyzersWereNotConfigured() {
        FieldBasedAnalyzerStrategy strategy = new FieldBasedAnalyzerStrategy();

        assertThatThrownBy(() -> strategy.getAnalyzer("title"))
                .isInstanceOf(NullPointerException.class);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
