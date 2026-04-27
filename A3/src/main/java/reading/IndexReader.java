package reading;

import storage.invertedIndex.PostingList;

import java.util.List;
import java.util.Map;

public interface IndexReader {
    Map<String, PostingList> getPosting(List<String> terms);
    QueryContext buildContext(List<String> terms);
}
