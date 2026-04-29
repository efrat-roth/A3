package analyzing.analyzerStrategy;

import analyzing.Analyzer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import storage.Field;

import java.util.List;

@NoArgsConstructor
public class DefaultAnalyzerStrategy implements AnalyzerStrategy {
    @Getter
    private Analyzer analyzer;

    @Override
    public Analyzer getAnalyzer(String field) {

        return analyzer;
    }
}
