package analyzing.analyzerStrategy;

import analyzing.Analyzer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import storage.Field;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FieldBasedAnalyzerStrategy implements AnalyzerStrategy {
    @Getter
    private final Map<String, Analyzer> analyzers;

    @Override
    public Analyzer getAnalyzer(String field) {

        return analyzers.get(field);
    }
}
