package org.storage.invertedIndex;

import java.util.Map;

public interface InvertedIndex {
    //String: FieldName, String2: term, String3:docId

    void addField(String fieldName, String token, String docId, int position, int docLength);

    void addTerm(String fieldName, String token, String docId, int position, int docLength) ;

    PostingList getPostingListByTerm(String fieldName, String term);
    Map<String,PostingList> getPostings(String fieldName);
}



