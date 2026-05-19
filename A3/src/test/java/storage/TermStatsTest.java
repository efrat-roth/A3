package storage;

import org.junit.jupiter.api.Test;
import org.storage.invertedIndex.TermStats;
import org.utils.Exceptions.InvalidTermStatsException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TermStatsTest {

    @Test
    void constructorShouldThrowWhenTfIsNegative() {
        assertThatThrownBy(() -> new TermStats(-0.1, List.of(1)))
                .isInstanceOf(InvalidTermStatsException.class)
                .hasMessage("Term frequency cannot be negative: -0.1");
    }

    @Test
    void constructorShouldThrowWhenPositionsAreNull() {
        assertThatThrownBy(() -> new TermStats(0.1, null))
                .isInstanceOf(InvalidTermStatsException.class)
                .hasMessage("Positions cannot be null");
    }

    @Test
    void constructorShouldThrowWhenPositionsContainInvalidValue() {
        assertThatThrownBy(() -> new TermStats(0.1, new ArrayList<>(List.of(-1))))
                .isInstanceOf(InvalidTermStatsException.class)
                .hasMessage("Positions cannot contain null or negative values");
    }

    @Test
    void incrementTfShouldThrowWhenIncrementIsNegative() {
        TermStats termStats = new TermStats(0.1, List.of(1));

        assertThatThrownBy(() -> termStats.incrementTf(-0.1))
                .isInstanceOf(InvalidTermStatsException.class)
                .hasMessage("Term frequency increment cannot be negative: -0.1");
    }
}
