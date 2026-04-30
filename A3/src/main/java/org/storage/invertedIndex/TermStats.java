package org.storage.invertedIndex;

import lombok.Getter;
import org.utils.Exceptions.InvalidTermStatsException;

import java.util.List;

public class TermStats {
    @Getter
    private double tf;
    @Getter
    private List<Integer> positions;

    public TermStats(double tf, List<Integer> positions) {
        if (tf < 0) {
            throw new InvalidTermStatsException("Term frequency cannot be negative: " + tf);
        }
        if (positions == null) {
            throw new InvalidTermStatsException("Positions cannot be null");
        }
        if (positions.stream().anyMatch(position -> position == null || position < 0)) {
            throw new InvalidTermStatsException("Positions cannot contain null or negative values");
        }
        this.tf = tf;
        this.positions = positions;
    }

    public void setTf(double tf) {
        if (tf < 0) {
            throw new InvalidTermStatsException("Term frequency cannot be negative: " + tf);
        }
        this.tf = tf;
    }

    public void incrementTf(double x){
        if (x < 0) {
            throw new InvalidTermStatsException("Term frequency increment cannot be negative: " + x);
        }
        setTf(tf + x);
    }

}
