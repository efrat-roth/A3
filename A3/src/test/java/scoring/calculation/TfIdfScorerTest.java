package scoring.calculation;

import org.junit.jupiter.api.Test;
import org.reading.QueryContext;
import org.scoring.ScoreResult;
import org.scoring.calculation.TfIdfScorer;
import org.storage.invertedIndex.TermStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class TfIdfScorerTest {

    @Test
    void calculateScoresShouldReturnScoreForDocument() {
        TfIdfScorer scorer = new TfIdfScorer();
        QueryContext context = new QueryContext(
                "doc-1",
                List.of(termStats(0.25), termStats(0.5)),
                Map.of("java", 2, "search", 4),
                10
        );

        ScoreResult result = scorer.calculateScores(context);

        double expectedTf = 0.25 + 0.5;
        double expectedIdf = Math.log(10 / 2.0) + Math.log(10 / 4.0);
        assertThat(result.docId()).isEqualTo("doc-1");
        assertThat(result.totalScore()).isCloseTo(expectedTf * expectedIdf, within(0.000001));
    }

    @Test
    void calculateScoresShouldReturnZeroWhenThereAreNoTermStats() {
        TfIdfScorer scorer = new TfIdfScorer();
        QueryContext context = new QueryContext(
                "doc-1",
                List.of(),
                Map.of("java", 2),
                10
        );

        ScoreResult result = scorer.calculateScores(context);

        assertThat(result).isEqualTo(new ScoreResult("doc-1", 0.0));
    }

    @Test
    void calculateScoresShouldReturnZeroWhenTermAppearsInEveryDocument() {
        TfIdfScorer scorer = new TfIdfScorer();
        QueryContext context = new QueryContext(
                "doc-1",
                List.of(termStats(0.75)),
                Map.of("java", 10),
                10
        );

        ScoreResult result = scorer.calculateScores(context);

        assertThat(result).isEqualTo(new ScoreResult("doc-1", 0.0));
    }

    @Test
    void calculateScoresShouldSumAllTermFrequenciesBeforeMultiplyingBySummedIdf() {
        TfIdfScorer scorer = new TfIdfScorer();
        QueryContext context = new QueryContext(
                "doc-2",
                List.of(termStats(0.1), termStats(0.2), termStats(0.3)),
                Map.of("a", 1, "b", 5),
                20
        );

        ScoreResult result = scorer.calculateScores(context);

        double expectedTf = 0.1 + 0.2 + 0.3;
        double expectedIdf = Math.log(20 / 1.0) + Math.log(20 / 5.0);
        assertThat(result.totalScore()).isCloseTo(expectedTf * expectedIdf, within(0.000001));
    }

    private static TermStats termStats(double tf) {
        return new TermStats(tf, new ArrayList<>(List.of(1)));
    }
}
