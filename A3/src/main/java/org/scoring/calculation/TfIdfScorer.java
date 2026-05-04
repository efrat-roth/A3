package org.scoring.calculation;

import lombok.extern.slf4j.Slf4j;
import org.reading.QueryContext;
import org.scoring.ScoreResult;
import org.storage.invertedIndex.TermStats;
import org.utils.Exceptions;

@Slf4j
public class TfIdfScorer implements ScoreCalculator {

    @Override
    public ScoreResult calculateScores(QueryContext context) {

        validateContext(context);

        log.debug("Calculating TF-IDF score: docId {}, termStatsCount {}, termsDfCount {}, docsCount {}",
                context.getDocId(), context.getStatsOfDoc().size(), context.getTermsDf().size(), context.getDocsCount());

        double tf = context.getStatsOfDoc().stream().mapToDouble(TermStats::getTf).sum();

        double idf = context.getTermsDf().values().stream()
                .mapToDouble(df -> Math.log(context.getDocsCount() / (double) df)).sum();


        double totalScore = tf * idf;

        log.debug("TF-IDF score calculated: docId {}, tf {}, idf {}, totalScore {}", context.getDocId(), tf, idf, totalScore);

        return new ScoreResult(context.getDocId(), totalScore);
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

        if (context.getStatsOfDoc() == null || context.getStatsOfDoc().isEmpty()) {
            throw new Exceptions.InvalidTermStatsException("Term statistics cannot be null or empty");
        }

        if (context.getTermsDf() == null || context.getTermsDf().isEmpty()) {
            throw new Exceptions.TermNotFoundException("No term document frequency data available");
        }

        boolean hasInvalidDf = context.getTermsDf().values().stream().anyMatch(df -> df <= 0);

        if (hasInvalidDf) {
            throw new Exceptions.InvalidTermStatsException("Document frequency must be greater than zero");
        }
    }
}