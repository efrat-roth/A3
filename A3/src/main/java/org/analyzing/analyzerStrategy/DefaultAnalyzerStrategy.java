package org.analyzing.analyzerStrategy;

import lombok.extern.slf4j.Slf4j;
import org.analyzing.Analyzer;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.utils.Exceptions;

@Slf4j
public class DefaultAnalyzerStrategy implements AnalyzerStrategy {

    private final Analyzer analyzer;
    public DefaultAnalyzerStrategy(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public Analyzer getAnalyzer(String field) {

        if (analyzer == null) {
            throw new Exceptions.AnalyzerConfigurationException("Default analyzer is not configured");
        }
        return analyzer;
    }
}