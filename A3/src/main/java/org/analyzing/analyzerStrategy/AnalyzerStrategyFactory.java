package org.analyzing.analyzerStrategy;

import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class AnalyzerStrategyFactory {

    private final Map<String, AnalyzerStrategy> analyzers = new HashMap<>();

    public AnalyzerStrategyFactory() {
        analyzers.put("fieldBased", new FieldBasedAnalyzerStrategy());
        analyzers.put("default", new DefaultAnalyzerStrategy());
    }

    public AnalyzerStrategy get(String name) {

        if (name == null || name.isBlank()) {
            throw new Exceptions.UnsupportedAnalyzerStrategyException("Analyzer strategy name cannot be null or blank");
        }
        Supplier<AnalyzerStrategy> supplier = analyzers.get(name);
        if (supplier == null) {
            throw new Exceptions.UnsupportedAnalyzerStrategyException("Unknown analyzer strategy: " + name);
        }
        return supplier.get();
    }
}