package org.analyzing.analyzerStrategy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Analyzer;

@Slf4j
@AllArgsConstructor
public class DefaultAnalyzerStrategy extends AnalyzerStrategy {
    @Getter
    private final Analyzer analyzer;
}