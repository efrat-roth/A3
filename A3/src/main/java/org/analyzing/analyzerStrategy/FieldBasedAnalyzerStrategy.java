package org.analyzing.analyzerStrategy;

import org.analyzing.Analyzer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@NoArgsConstructor
public class FieldBasedAnalyzerStrategy implements AnalyzerStrategy {
    @Getter
    private Map<String, Analyzer> analyzers;

    @Override
    public Analyzer getAnalyzer(String field) {
        log.debug("Retrieving field-based analyzer: field {}", field);
        if (analyzers == null) {
            log.warn("Field-based analyzers have not been initialized: field {}", field);
        } else if (!analyzers.containsKey(field)) {
            log.warn("Analyzer not found for field: {}", field);
        }
        Analyzer analyzer = analyzers.get(field);
        log.debug("Field-based analyzer retrieved: field {}", field);
        return analyzer;
    }
}
