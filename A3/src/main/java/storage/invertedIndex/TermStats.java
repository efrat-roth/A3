package storage.invertedIndex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class TermStats {
    @Getter
    @Setter
    private double tf;
    @Getter
    private List<Integer> positions;
    public void incrementTf(double x){
        setTf(tf + x);
    }

}
