package org.analyzing.analyzerStrategy;

import org.analyzing.Analyzer;

public interface AnalyzerStrategy {
    Analyzer getAnalyzer(String field);
}
