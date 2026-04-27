package storage.invertedIndex;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface InvertedIndex {
    //String: FieldName, String2: term, String3:docId

    public void addField(String fieldName, String token, String docId, int position, int docLength);

    public void addTerm(String fieldName, String token, String docId, int position, int docLength) ;

    public PostingList getPostingList(String fieldName, String term);
}



