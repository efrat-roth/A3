package analyzing.analyzerStrategy;

import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.analyzing.analyzerStrategy.AnalyzerStrategyFactory;
import org.analyzing.analyzerStrategy.DefaultAnalyzerStrategy;
import org.analyzing.analyzerStrategy.FieldBasedAnalyzerStrategy;
import org.junit.jupiter.api.Test;
import org.utils.config.AppConfig;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AnalyzerStrategyFactoryTest {
    AppConfig appConfig = new AppConfig();
    @Test
    void getShouldReturnDefaultAnalyzerStrategyForDefaultName() throws IOException {

        AnalyzerStrategyFactory factory = new AnalyzerStrategyFactory(appConfig);

        AnalyzerStrategy strategy = factory.get("default");

        assertThat(strategy).isInstanceOf(DefaultAnalyzerStrategy.class);
    }

    @Test
    void getShouldReturnFieldBasedAnalyzerStrategyForFieldBasedName() throws IOException {
        AnalyzerStrategyFactory factory = new AnalyzerStrategyFactory(appConfig);

        AnalyzerStrategy strategy = factory.get("fieldBased");

        assertThat(strategy).isInstanceOf(FieldBasedAnalyzerStrategy.class);
    }

    @Test
    void getShouldReturnNewStrategyInstanceEachTime() throws IOException {
        AnalyzerStrategyFactory factory = new AnalyzerStrategyFactory(appConfig);

        AnalyzerStrategy firstStrategy = factory.get("default");
        AnalyzerStrategy secondStrategy = factory.get("default");

        assertThat(firstStrategy).isNotSameAs(secondStrategy);
    }

    @Test
    void getShouldThrowNullPointerExceptionForUnknownStrategyName() throws IOException {
        AnalyzerStrategyFactory factory = new AnalyzerStrategyFactory(appConfig);

        assertThatThrownBy(() -> factory.get("unknown"))
                .isInstanceOf(NullPointerException.class);
    }
}
