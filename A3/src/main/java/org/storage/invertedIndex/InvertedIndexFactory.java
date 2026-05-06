package org.storage.invertedIndex;


import org.utils.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class InvertedIndexFactory {

    private final Map<String, Supplier<InvertedIndex>> registry = new HashMap<>();

    public InvertedIndexFactory() {
        registry.put("inMemory", InMemoryInvertedIndex::new);
    }

    public InvertedIndex create(String type) {
        if (type == null || type.isBlank()) {
            throw new Exceptions.InvalidIndexEntryException("Inverted index type cannot be null or blank");
        }

        Supplier<InvertedIndex> supplier = registry.get(type);

        if (supplier == null) {
            throw new Exceptions.InvalidIndexEntryException("Unknown inverted index type: " + type);
        }

        return supplier.get();
    }
}