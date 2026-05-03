package org.analyzing.analyzerStrategy;

import org.utils.config.AppConfig;

import java.io.IOException;

public class AnalyzerProvider {

    private final AppConfig config;
    private final AnalyzerStrategyFactory analyzerStrategyFactory;

    public AnalyzerProvider(AppConfig config) throws IOException {
        this.config = config;
        analyzerStrategyFactory = new AnalyzerStrategyFactory(config);
    }

    public AnalyzerStrategy provide() throws IOException {

        return analyzerStrategyFactory.get(config.analyzer.getStrategy());

    }


}