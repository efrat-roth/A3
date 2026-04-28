package storage.invertedIndex;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryInvertedIndex implements InvertedIndex {
    @Getter
    private Map<String, Map<String, PostingList>> index = new HashMap<>();

    public void addField(String fieldName, String token, String docId, int position, int docLength) {
        Map<String, PostingList> result = index.putIfAbsent(fieldName, Map.of(token, new PostingList(
                Map.of(docId, new TermStats(1.0 / docLength, new ArrayList<>(List.of(position)))))));
        if (result == null)
            addTerm(fieldName, token, docId, position, docLength);

    }

    public void addTerm(String fieldName, String token, String docId, int position, int docLength) {
        if (!index.containsKey(fieldName))
            addField(fieldName, token, docId, position, docLength);
        Map<String, PostingList> fieldEntry = index.get(fieldName);
        if (!fieldEntry.containsKey(token)) {
            fieldEntry.put(token, new PostingList(Map.of(docId, new TermStats(1, List.of(position)))));
        } else {
            addDoc(fieldEntry, token, docId, position, docLength);
        }

    }

    private void addDoc(Map<String, PostingList> fieldEntry, String token, String docId, int position, int docLength) {
        Map<String, TermStats> tokenEntry = fieldEntry.get(token).getPostings();
        //if doc is already exist
        //improve time running with the list - for thinking if sorted list is needed
        if (tokenEntry.containsKey(docId)) {
            tokenEntry.get(docId).incrementTf(1.0 / docLength);
            tokenEntry.get(docId).getPositions().add(position);
        } else
            tokenEntry.put(docId, new TermStats(1.0 / docLength, List.of(position)));
    }
    public PostingList getPostingListByTerm(String fieldName, String term){

        return index.get(fieldName).get(term);
    }
    public Map<String,PostingList> getPostings(String fieldName){
        return index.get(fieldName); }
}
