package analyzing.analyzerStrategy;

import analyzing.Analyzer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import storage.Field;

import java.util.List;

@RequiredArgsConstructor
public class DefaultAnalyzerStrategy implements AnalyzerStrategy {
    @Getter
    private final Analyzer analyzer;

    @Override
    public Analyzer getAnalyzer(String field) {

        return analyzer;
    }
}
