package org.storage.invertedIndex;


import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;
import org.utils.config.AppConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class InvertedIndexFactory {

    private final Map<String, Supplier<InvertedIndex>> registry = new HashMap<>();
    private final AppConfig config;

    public InvertedIndexFactory(AppConfig config) {
        registry.put("inMemory", InMemoryInvertedIndex::new);
        this.config = config;
        log.debug("Registered memory types: {}", registry.keySet());
    }

    public InvertedIndex create() {
        String type = config.storage.getType();
        log.debug("Retrieving memory type {}", type);
       Supplier<InvertedIndex> supplier = registry.get(type);

        if (supplier == null) {
            log.warn("Unknown inverted index requested: {}", type);
            throw new Exceptions.InvalidIndexEntryException("Unknown inverted index type: " + type);
        }

        return supplier.get();
    }
}