package analyzing.analyzerStrategy;

import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.analyzing.analyzerStrategy.AnalyzerStrategyFactory;
import org.analyzing.analyzerStrategy.DefaultAnalyzerStrategy;
import org.analyzing.analyzerStrategy.FieldBasedAnalyzerStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AnalyzerStrategyFactoryTest {

    @Test
    void getShouldReturnDefaultAnalyzerStrategyForDefaultName() {
        AnalyzerStrategyFactory factory = new AnalyzerStrategyFactory();

        AnalyzerStrategy strategy = factory.get("default");

        assertThat(strategy).isInstanceOf(DefaultAnalyzerStrategy.class);
    }

    @Test
    void getShouldReturnFieldBasedAnalyzerStrategyForFieldBasedName() {
        AnalyzerStrategyFactory factory = new AnalyzerStrategyFactory();

        AnalyzerStrategy strategy = factory.get("fieldBased");

        assertThat(strategy).isInstanceOf(FieldBasedAnalyzerStrategy.class);
    }

    @Test
    void getShouldReturnNewStrategyInstanceEachTime() {
        AnalyzerStrategyFactory factory = new AnalyzerStrategyFactory();

        AnalyzerStrategy firstStrategy = factory.get("default");
        AnalyzerStrategy secondStrategy = factory.get("default");

        assertThat(firstStrategy).isNotSameAs(secondStrategy);
    }

    @Test
    void getShouldThrowNullPointerExceptionForUnknownStrategyName() {
        AnalyzerStrategyFactory factory = new AnalyzerStrategyFactory();

        assertThatThrownBy(() -> factory.get("unknown"))
                .isInstanceOf(NullPointerException.class);
    }
}
