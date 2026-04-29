package analyzing.analyzerStrategy;

import org.analyzing.Analyzer;
import org.analyzing.analyzerStrategy.DefaultAnalyzerStrategy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultAnalyzerStrategyTest {

    @Test
    void getAnalyzerShouldReturnConfiguredAnalyzerForAnyField() throws Exception {
        DefaultAnalyzerStrategy strategy = new DefaultAnalyzerStrategy();
        Analyzer analyzer = Analyzer.builder().build();
        setPrivateField(strategy, "analyzer", analyzer);

        assertThat(strategy.getAnalyzer("title")).isSameAs(analyzer);
        assertThat(strategy.getAnalyzer("body")).isSameAs(analyzer);
    }

    @Test
    void getAnalyzerShouldReturnNullWhenAnalyzerWasNotConfigured() {
        DefaultAnalyzerStrategy strategy = new DefaultAnalyzerStrategy();

        assertThat(strategy.getAnalyzer("title")).isNull();
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
