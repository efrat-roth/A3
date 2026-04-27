package reading;

import lombok.AllArgsConstructor;
import storage.IndexStorage;
import storage.invertedIndex.PostingList;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class InMemoryIndexReader implements IndexReader {
    private final IndexStorage indexStorage;

    @Override
    public Map<String, PostingList> getPosting(String fieldName) {
        return indexStorage.getInvertedIndex().getPostings(fieldName);
    }

    @Override
    public QueryContext buildContext(List<String> terms) {

    }

}
