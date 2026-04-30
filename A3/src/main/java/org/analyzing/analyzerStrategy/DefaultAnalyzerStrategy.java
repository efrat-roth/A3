package org.analyzing.analyzerStrategy;

import org.analyzing.Analyzer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class DefaultAnalyzerStrategy implements AnalyzerStrategy {
    @Getter
    private Analyzer analyzer;

    @Override
    public Analyzer getAnalyzer(String field) {
        log.debug("Retrieving default analyzer for field: {}", field);
        if (analyzer == null) {
            log.warn("Default analyzer has not been initialized for field: {}", field);
        }
        return analyzer;
    }
}
