package org.analyzing.analyzerStrategy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Analyzer;
import org.utils.Exceptions;

@Slf4j
@AllArgsConstructor
public class DefaultAnalyzerStrategy implements AnalyzerStrategy {

    private final Analyzer analyzer;

    @Override
    public Analyzer getAnalyzer(String field) {

        if (analyzer == null) {
            throw new Exceptions.AnalyzerConfigurationException("Default analyzer is not configured");
        }
        return analyzer;
    }
}