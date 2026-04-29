package org.analyzing.analyzerStrategy;

import org.analyzing.Analyzer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
public class FieldBasedAnalyzerStrategy implements AnalyzerStrategy {
    @Getter
    private Map<String, Analyzer> analyzers;

    @Override
    public Analyzer getAnalyzer(String field) {

        return analyzers.get(field);
    }
}
