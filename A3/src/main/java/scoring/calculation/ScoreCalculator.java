package scoring.calculation;

import reading.QueryContext;
import scoring.ScoreResult;

import java.util.List;

public interface ScoreCalculator {
    List<ScoreResult> calculateScores(QueryContext queryContext);
}
