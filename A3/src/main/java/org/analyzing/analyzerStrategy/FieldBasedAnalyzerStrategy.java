package org.analyzing.analyzerStrategy;

import lombok.extern.slf4j.Slf4j;
import org.analyzing.Analyzer;
import org.utils.Exceptions;

import java.util.Map;

@Slf4j
public class FieldBasedAnalyzerStrategy implements AnalyzerStrategy {

    private final Map<String, Analyzer> analyzers;

    public FieldBasedAnalyzerStrategy(Map<String, Analyzer> analyzers) {
        this.analyzers = analyzers;
    }

    @Override
    public Analyzer getAnalyzer(String field) {

        if (analyzers == null || analyzers.isEmpty()) {
            throw new Exceptions.AnalyzerConfigurationException("Field analyzers are not configured");
        }

        if (analyzers.get(field) == null) {
            throw new Exceptions.AnalyzerNotFoundException("No analyzer configured for field: " + field);
        }

        return analyzers.get(field);
    }
}