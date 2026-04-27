package scoring;

import reading.QueryContext;

import java.util.List;

public interface ScoreCalculator {
    List<ScoreResult> calculateScores(QueryContext queryContext);
}
