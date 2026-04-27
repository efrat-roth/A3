package reading;

import lombok.Data;
import storage.invertedIndex.PostingList;

import java.util.List;
import java.util.Map;

@Data
public class QueryContext {
    private List<String> terms;
    private Map<String, PostingList> stats;
    private int totalDocs;
    private Map<String, Integer> docFreq;
}
