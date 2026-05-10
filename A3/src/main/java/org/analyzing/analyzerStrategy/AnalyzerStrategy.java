package org.analyzing.analyzerStrategy;

import org.analyzing.Analyzer;

public interface AnalyzerStrategy {
    public Analyzer getAnalyzer(String field);
}
