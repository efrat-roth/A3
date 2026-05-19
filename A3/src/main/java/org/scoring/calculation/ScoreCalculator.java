package org.scoring.calculation;

import org.reading.QueryContext;
import org.scoring.ScoreResult;

public interface ScoreCalculator {
   ScoreResult calculateScores(QueryContext queryContext);
}
