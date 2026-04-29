package org.scoring.calculation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ScoreRegistry {

    private final Map<String, Supplier<ScoreCalculator>> scorers =
            new HashMap<>();

    public ScoreRegistry() {
        scorers.put("tfidf", TfIdfScorer::new);
    }

    public ScoreCalculator get(String name) {

        Supplier<ScoreCalculator> supplier = scorers.get(name);

        if (supplier == null) {
            throw new IllegalArgumentException(
                    "Unknown scorer: " + name
            );
        }

        return supplier.get();
    }
}
