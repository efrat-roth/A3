package org.analyzing.analyzerStrategy;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class AnalyzerStrategyFactory {
    private final Map<String, Supplier<AnalyzerStrategy>> analyzers =
            new HashMap<>();

    public AnalyzerStrategyFactory() {
        analyzers.put("fieldBased", FieldBasedAnalyzerStrategy::new);
        analyzers.put("default", DefaultAnalyzerStrategy::new);
        log.debug("Registered analyzer strategies: {}", analyzers.keySet());
    }

    public AnalyzerStrategy get(String name) {
        log.debug("Retrieving analyzer strategy: {}", name);
        Supplier<AnalyzerStrategy> supplier = analyzers.get(name);
        if (supplier == null) {
            log.warn("Unknown analyzer strategy requested: {}", name);
        }
        AnalyzerStrategy analyzerStrategy = supplier.get();
        log.debug("Analyzer strategy created: {}", name);
        return analyzerStrategy;
    }
}
