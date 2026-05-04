package org.scoring.calculation;

import org.utils.config.AppConfig;

import java.io.IOException;

public class ScoreProvider {

    private final AppConfig config;
    private final ScoreRegistry scoreRegistry;

    public ScoreProvider(AppConfig config) throws IOException {
        this.config = config;
        scoreRegistry = new ScoreRegistry();
    }

    public ScoreCalculator provide() throws IOException {

        return scoreRegistry.get(config.index.getScoringAlgorithm());

    }

}