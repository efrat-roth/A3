package org.reading;

import lombok.Data;
import org.storage.invertedIndex.TermStats;

import java.util.List;
import java.util.Map;

@Data
public class QueryContext {
    private final String docId;
    private final List<TermScoreDTO> termScoreDTO;
    //private final Map<String,TermStats> statsOfDoc;
    //private final Map<String, Integer> termsDf;
    private final int docsCount;
}
