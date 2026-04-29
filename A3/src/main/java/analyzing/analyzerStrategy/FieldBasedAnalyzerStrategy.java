package analyzing.analyzerStrategy;

import analyzing.Analyzer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import storage.Field;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class FieldBasedAnalyzerStrategy implements AnalyzerStrategy {
    @Getter
    private Map<String, Analyzer> analyzers;

    @Override
    public Analyzer getAnalyzer(String field) {

        return analyzers.get(field);
    }
}
