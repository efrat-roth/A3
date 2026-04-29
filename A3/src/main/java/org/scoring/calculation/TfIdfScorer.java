package org.scoring.calculation;

import org.reading.QueryContext;
import org.scoring.ScoreResult;
import org.storage.invertedIndex.TermStats;

public class TfIdfScorer implements ScoreCalculator {
    @Override
    public ScoreResult calculateScores(QueryContext context) {
        double tf = context.getStatsOfDoc().stream().mapToDouble(TermStats::getTf).sum();
        double idf = context.getTermsDf().values().stream().mapToDouble(
                dfOfTerm -> (Math.log(context.getDocsCount() / (double) dfOfTerm))).sum();
        return new ScoreResult(context.getDocId(), tf * idf);
    }
}
