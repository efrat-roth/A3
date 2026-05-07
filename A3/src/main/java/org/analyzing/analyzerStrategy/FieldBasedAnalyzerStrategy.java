package org.analyzing.analyzerStrategy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Analyzer;

import java.util.Map;

@Slf4j
public class FieldBasedAnalyzerStrategy extends AnalyzerStrategy {
    @Getter
    private final Map<String, Analyzer> analyzers;

    public FieldBasedAnalyzerStrategy(Map<String, Analyzer> analyzers) {

        this.analyzers = analyzers;
    }
}