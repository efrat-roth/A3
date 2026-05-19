package org.analyzing.analyzerStrategy;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Analyzer;
import org.utils.Exceptions;

import java.util.Map;

@Slf4j
public class FieldBasedAnalyzerStrategy implements AnalyzerStrategy {
    @NonNull
    private final Map<String, Analyzer> analyzers;

    public FieldBasedAnalyzerStrategy(Map<String, Analyzer> analyzers) {
        if (analyzers.containsValue(null) || analyzers.containsKey(null)) {
            throw new Exceptions.AnalyzerNotFoundException("Map has a null value");
        }
        this.analyzers = analyzers;
    }

    @Override
    public Analyzer getAnalyzer(String field) {
        return analyzers.get(field);
    }
}