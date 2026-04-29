package org.storage.invertedIndex;

import java.util.Map;

public interface InvertedIndex {
    //String: FieldName, String2: term, String3:docId

    public void addField(String fieldName, String token, String docId, int position, int docLength);

    public void addTerm(String fieldName, String token, String docId, int position, int docLength) ;

    public PostingList getPostingListByTerm(String fieldName, String term);
    public  Map<String,PostingList> getPostings(String fieldName);
}



