package org.analyzing.charFilters;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class CharFilterRegistry {
    private final Map<String, Supplier<CharFilter>> filters =
            new HashMap<>();

    public CharFilterRegistry() {
        filters.put("lowercase", LowercaseCharFilter::new);
        filters.put("punctuation", PunctuationCharFilter::new);
        log.debug("Registered char filters: {}", filters.keySet());
    }
    public CharFilter get(String name) {
        log.debug("Retrieving char filter: {}", name);
        Supplier<CharFilter> supplier = filters.get(name);
        if (supplier == null) {
            log.warn("Unknown char filter requested: {}", name);
        }
        CharFilter charFilter = supplier.get();
        log.debug("Char filter created: {}", name);
        return charFilter;
    }
}
