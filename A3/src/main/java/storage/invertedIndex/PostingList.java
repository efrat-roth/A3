package storage.invertedIndex;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PostingList {
    @Getter
    private Map<String, TermStats> postings = new HashMap<>();
}
