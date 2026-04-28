package reading;

import lombok.Data;
import storage.invertedIndex.PostingList;
import storage.invertedIndex.TermStats;

import java.util.List;
import java.util.Map;

@Data
public class QueryContext {
    private final String docId;
    private final List<TermStats> statsOfDoc;
    private final Map<String, Integer> termsDf;
    private final int docsCount;
}
