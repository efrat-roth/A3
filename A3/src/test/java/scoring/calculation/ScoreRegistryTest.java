package scoring.calculation;

import org.junit.jupiter.api.Test;
import org.scoring.calculation.ScoreCalculator;
import org.scoring.calculation.ScoreRegistry;
import org.scoring.calculation.TfIdfScorer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ScoreRegistryTest {

    @Test
    void getShouldReturnTfIdfScorerForTfidfName() {
        ScoreRegistry registry = new ScoreRegistry();

        ScoreCalculator scorer = registry.get("tfidf");

        assertThat(scorer).isInstanceOf(TfIdfScorer.class);
    }

    @Test
    void getShouldReturnNewScorerInstanceEachTime() {
        ScoreRegistry registry = new ScoreRegistry();

        ScoreCalculator firstScorer = registry.get("tfidf");
        ScoreCalculator secondScorer = registry.get("tfidf");

        assertThat(firstScorer).isNotSameAs(secondScorer);
    }

    @Test
    void getShouldThrowIllegalArgumentExceptionForUnknownScorerName() {
        ScoreRegistry registry = new ScoreRegistry();

        assertThatThrownBy(() -> registry.get("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown scorer: unknown");
    }
}
