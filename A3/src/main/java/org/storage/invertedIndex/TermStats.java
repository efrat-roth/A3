package org.storage.invertedIndex;

import lombok.Getter;
import lombok.Setter;

public class TermStats {
    @Getter
    @Setter
    private int df = 0;
    @Getter
    @Setter
    private int totalTf = 0;

    public void incrementDf() {
        df++;
    }

    public void addTf(int tf) {
        totalTf += tf;
    }
}
