package org.indexing;

import lombok.extern.slf4j.Slf4j;
import org.analyzing.analyzerStrategy.AnalyzerStrategy;
import org.storage.IndexStorage;

@Slf4j
public class InMemoryIndexWriter extends IndexWriter {


    public InMemoryIndexWriter(AnalyzerStrategy analyzerStrategy, IndexStorage indexStorage) {
        super(analyzerStrategy, indexStorage);
    }
}