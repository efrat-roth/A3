package org.scoring.calculation;

import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class ScoreRegistry {

    private final Map<String, Supplier<ScoreCalculator>> scorers = new HashMap<>();

    public ScoreRegistry() {
        scorers.put("tfIdf", TfIdfScorer::new);
        log.debug("Registered scorers: {}", scorers.keySet());
    }

    public ScoreCalculator get(String name) {

        log.debug("Retrieving scorer: {}", name);

        Supplier<ScoreCalculator> supplier = scorers.get(name);

        if (supplier == null) {
            log.warn("Unknown scorer requested: {}", name);
            throw new Exceptions.UnsupportedScoringAlgorithmException("Unsupported scoring algorithm: " + name);
        }

        log.debug("Scorer found: {}", name);
        return supplier.get();
    }
}