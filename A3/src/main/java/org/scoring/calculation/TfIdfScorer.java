package org.scoring.calculation;

import lombok.extern.slf4j.Slf4j;
import org.reading.QueryContext;
import org.scoring.ScoreResult;
import org.storage.invertedIndex.TermStats;

@Slf4j
public class TfIdfScorer implements ScoreCalculator {
    @Override
    public ScoreResult calculateScores(QueryContext context) {
        log.debug("Calculating TF-IDF score: docId {}, termStatsCount {}, termsDfCount {}, docsCount {}",
                context.getDocId(), context.getStatsOfDoc().size(), context.getTermsDf().size(), context.getDocsCount());
        double tf = context.getStatsOfDoc().stream().mapToDouble(TermStats::getTf).sum();
        double idf = context.getTermsDf().values().stream().mapToDouble(
                dfOfTerm -> (Math.log(context.getDocsCount() / (double) dfOfTerm))).sum();
        double totalScore = tf * idf;
        log.debug("TF-IDF score calculated: docId {}, tf {}, idf {}, totalScore {}", context.getDocId(), tf, idf, totalScore);
        return new ScoreResult(context.getDocId(), totalScore);
    }
}
