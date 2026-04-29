package org.indexing;

import org.storage.Field;

import java.util.List;

public interface IndexWriter {
    public void addDocument(List<Field> document);
}
