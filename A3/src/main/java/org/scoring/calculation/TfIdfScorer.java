package org.scoring.calculation;

import lombok.extern.slf4j.Slf4j;
import org.reading.QueryContext;
import org.reading.TermScoreDTO;
import org.scoring.ScoreResult;
import org.utils.Exceptions;

@Slf4j
public class TfIdfScorer implements ScoreCalculator {

    @Override
    public ScoreResult calculateScores(QueryContext context) {

        validateContext(context);

        log.debug("Calculating TF-IDF score: docId {}, TermScoreDTO {}, docsCount {}",
                context.getDocId(), context.getTermScoreDTO().size(), context.getDocsCount());
        double score = 0.0;

        for (TermScoreDTO entry : context.getTermScoreDTO()) {
            double tf = entry.tf().getTf();
            double idf = entry.idf();
            score += tf * idf;
        }
        log.debug("TF-IDF score calculated: docId {},  totalScore {}", context.getDocId(), score);

        return new ScoreResult(context.getDocId(), score);
    }

    private void validateContext(QueryContext context) {

        if (context == null) {
            throw new Exceptions.InvalidTermStatsException("QueryContext cannot be null");
        }

        if (context.getDocId() == null || context.getDocId().isBlank()) {
            throw new Exceptions.DocumentNotFoundException("Document id is missing");
        }

        if (context.getDocsCount() <= 0) {
            throw new Exceptions.InvalidIndexEntryException("Documents count must be greater than zero");
        }

        if (context.getTermScoreDTO() == null || context.getTermScoreDTO().isEmpty()) {
            throw new Exceptions.TermNotFoundException("No term DTO data available");
        }

        boolean hasInvalidDf = context.getTermScoreDTO().stream().anyMatch(DTO -> DTO.idf() <= 0);

        if (hasInvalidDf) {
            throw new Exceptions.InvalidTermStatsException("Document frequency must be greater than zero");
        }
    }
}