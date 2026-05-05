package org.reading;

import org.storage.invertedIndex.TermStats;

public record TermScoreDTO(String term, TermStats tf, Double idf) {
}
