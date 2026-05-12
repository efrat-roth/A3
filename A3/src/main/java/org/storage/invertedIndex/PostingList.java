package org.storage.invertedIndex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class PostingList {
    @Getter
    private final Map<String, Posting> postings = new HashMap<>();
    @Getter
    private final TermStats stats = new TermStats();

    public void addOccurrence(String docId, int position) {
        Posting posting = postings.get(docId);

        if (posting == null) {
            posting = new Posting(docId, position);
            postings.put(docId, posting);
            stats.incrementDf();
        } else {
            posting.addPosition(position);
        }
        stats.addTf(1);
    }

}
