package scoring.calculation;

import reading.QueryContext;
import scoring.ScoreResult;

import java.util.List;

public class TfIdfScorer implements ScoreCalculator {
    @Override
    public List<ScoreResult> calculateScores(QueryContext queryContext) {
        return List.of();
    }
}
