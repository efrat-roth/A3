package reading;

import lombok.Data;
import storage.invertedIndex.PostingList;

import java.util.List;
import java.util.Map;

@Data
public class QueryContext {
    private final List<String> terms;
    private final Map<String, PostingList> stats;
    private final int totalDocs;
    private final Map<String, Integer> docFreq;
}
