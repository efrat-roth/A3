package org.storage.invertedIndex;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Posting {
    @Getter @NonNull
    private final String docId;
    @Getter @Setter
    private double tf;
    @Getter @NonNull
    private final List<Integer> positions;

    public Posting(@NonNull String docId, int position) {
        this.docId = docId;
        this.positions = new ArrayList<>();
        this.positions.add(position);
        this.tf = 1;
    }

    public void addPosition(int pos) {
        positions.add(pos);
        tf++;
    }

}
