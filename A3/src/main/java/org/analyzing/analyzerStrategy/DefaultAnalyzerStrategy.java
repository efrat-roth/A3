package org.analyzing.analyzerStrategy;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Analyzer;


@Slf4j
@AllArgsConstructor
public class DefaultAnalyzerStrategy implements AnalyzerStrategy {
    @NonNull
    private final Analyzer analyzer;

    @Override
    public Analyzer getAnalyzer(String field) {
        return analyzer;
    }
}