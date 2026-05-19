package org.reading;

import org.storage.FieldValue;
import org.storage.invertedIndex.PostingList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IndexReader {
    Optional<Map<String, PostingList>> getPosting(String fieldName);

    QueryContext buildContext(String docId, Map<String, List<String>> queryTermsByFields);

    List<FieldValue> getDocument(String docId);
}
