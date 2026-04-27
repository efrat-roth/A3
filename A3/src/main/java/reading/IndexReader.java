package reading;

import storage.Field;
import storage.invertedIndex.PostingList;

import java.util.List;
import java.util.Map;

public interface IndexReader {
    Map<String, PostingList> getPosting(Field field, List<String> terms);
    QueryContext buildContext(List<String> terms);
}
