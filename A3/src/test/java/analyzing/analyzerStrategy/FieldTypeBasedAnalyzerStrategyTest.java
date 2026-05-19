package analyzing.analyzerStrategy;

import org.analyzing.Analyzer;
import org.analyzing.analyzerStrategy.FieldBasedAnalyzerStrategy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FieldTypeBasedAnalyzerStrategyTest {

    @Test
    void getAnalyzerShouldReturnAnalyzerConfiguredForRequestedField() throws Exception {
        Map <String, Analyzer> analyzers = new HashMap<String, Analyzer>();
        Analyzer titleAnalyzer = Analyzer.builder().build();
        analyzers.put("title", titleAnalyzer);
        Analyzer bodyAnalyzer = Analyzer.builder().build();
        analyzers.put("body", bodyAnalyzer);
        FieldBasedAnalyzerStrategy strategy = new FieldBasedAnalyzerStrategy(analyzers);
        setPrivateField(strategy, "analyzers", Map.of(
                "title", titleAnalyzer,
                "body", bodyAnalyzer
        ));

        assertThat(strategy.getAnalyzer("title")).isSameAs(titleAnalyzer);
        assertThat(strategy.getAnalyzer("body")).isSameAs(bodyAnalyzer);
    }

    @Test
    void getAnalyzerShouldReturnNullWhenFieldIsNotConfigured() throws Exception {
        Map <String, Analyzer> analyzers = new HashMap<String, Analyzer>();
        analyzers.put("title", Analyzer.builder().build());
        FieldBasedAnalyzerStrategy strategy = new FieldBasedAnalyzerStrategy(analyzers);
        setPrivateField(strategy, "analyzers", Map.of("title", Analyzer.builder().build()));

        assertThat(strategy.getAnalyzer("missing")).isNull();
    }

    @Test
    void getAnalyzerShouldThrowNullPointerExceptionWhenAnalyzersWereNotConfigured() {
        Map <String, Analyzer> analyzers = new HashMap<String, Analyzer>();
        FieldBasedAnalyzerStrategy strategy = new FieldBasedAnalyzerStrategy(analyzers);

        assertThatThrownBy(() -> strategy.getAnalyzer("title"))
                .isInstanceOf(NullPointerException.class);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
