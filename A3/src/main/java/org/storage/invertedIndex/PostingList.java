package org.storage.invertedIndex;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class PostingList {
    @Getter
    private Map<String, TermStats> postings = new HashMap<>();
}
