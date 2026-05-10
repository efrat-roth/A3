package org.reading;

import lombok.Data;

import java.util.List;

@Data
public class QueryContext {
    private final String docId;
    private final List<TermScoreDTO> termScoreDTO;
}
