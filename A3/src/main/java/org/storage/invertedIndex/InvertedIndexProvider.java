package org.storage.invertedIndex;

import org.utils.config.AppConfig;

public class InvertedIndexProvider {

    private final InvertedIndexFactory factory = new InvertedIndexFactory();
    private final AppConfig config;

    public InvertedIndexProvider(AppConfig config) {
        this.config = config;
    }

    public InvertedIndex provide() {
        String type = config.storage.getType();
        return factory.create(type);
    }
}