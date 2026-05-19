package org.scoring.calculation;

import lombok.extern.slf4j.Slf4j;
import org.reading.QueryContext;
import org.reading.TermScoreDTO;
import org.scoring.ScoreResult;

@Slf4j
public class TfIdfScorer implements ScoreCalculator {

    @Override
    public ScoreResult calculateScores(QueryContext context) {

        log.debug("Calculating TF-IDF score: docId {}, TermScoreDTO {}",
                context.getDocId(), context.getTermScoreDTO().size());

        double score = 0.0;
        for (TermScoreDTO entry : context.getTermScoreDTO()) {
            double tf = entry.posting().getTf();
            double idf = entry.idf();
            score += tf * idf;
        }
        log.debug("TF-IDF score calculated: docId {},  totalScore {}", context.getDocId(), score);

        return new ScoreResult(context.getDocId(), score);
    }

}