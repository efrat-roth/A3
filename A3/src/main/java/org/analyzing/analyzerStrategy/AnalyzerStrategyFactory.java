package org.analyzing.analyzerStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AnalyzerStrategyFactory {
    private final Map<String, Supplier<AnalyzerStrategy>> analyzers =
            new HashMap<>();

    public AnalyzerStrategyFactory() {
        analyzers.put("fieldBased", FieldBasedAnalyzerStrategy::new);
        analyzers.put("default", DefaultAnalyzerStrategy::new);
    }

    public AnalyzerStrategy get(String name) {
        return analyzers.get(name).get();
    }
}
