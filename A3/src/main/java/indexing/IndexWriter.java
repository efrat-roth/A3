package indexing;

import storage.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IndexWriter {
    public void addDocument(List<Field> document);
}
