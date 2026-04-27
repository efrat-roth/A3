package reading;

import lombok.AllArgsConstructor;
import storage.Field;
import storage.IndexStorage;
import storage.invertedIndex.PostingList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class InMemoryIndexReader implements IndexReader {
    private final IndexStorage indexStorage;

    @Override
    public Map<String,Map<String, PostingList>> getPosting(String fieldName, String term) {
        indexStorage.getInvertedIndex().getPostingList(fieldName, term);
    }

    @Override
    public QueryContext buildContext(List<String> terms) {

    }

}
