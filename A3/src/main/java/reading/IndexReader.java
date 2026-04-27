package reading;

import storage.Field;
import storage.invertedIndex.PostingList;

import java.util.List;
import java.util.Map;

public interface IndexReader {
    Map<String, PostingList> getPosting(String fieldName);
    QueryContext buildContext(List<String> terms);
}
