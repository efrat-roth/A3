package org.reading;

import org.storage.Field;
import org.storage.invertedIndex.PostingList;

import java.util.List;
import java.util.Map;

public interface IndexReader {
    Map<String, PostingList> getPosting(String fieldName);
    QueryContext buildContext(String docId, Map<String, List<String>> queryTermsByFields);
    List<Field> getDocument(String docId);
}
