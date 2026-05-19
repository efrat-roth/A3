package org.reading;

import org.storage.invertedIndex.Posting;

public record TermScoreDTO(String term, Posting posting, Double idf) {
}
