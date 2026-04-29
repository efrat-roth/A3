package analyzing.analyzerStrategy;

import analyzing.Analyzer;
import storage.Field;

import java.util.List;

public interface AnalyzerStrategy {
    public Analyzer getAnalyzer(String field);
}
