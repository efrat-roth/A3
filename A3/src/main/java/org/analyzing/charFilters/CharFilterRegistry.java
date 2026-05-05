package org.analyzing.charFilters;

import lombok.extern.slf4j.Slf4j;
import org.analyzing.tokenFilters.TokenFilter;
import org.utils.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
public class CharFilterRegistry {

    private final Map<String, Supplier<CharFilter>> filters = new HashMap<>();
    private final Map<String, CharFilter> cache = new ConcurrentHashMap<>();


    public CharFilterRegistry() {
        filters.put("lowercase", LowercaseCharFilter::new);
        filters.put("punctuation", PunctuationCharFilter::new);
        log.debug("Registered char filters: {}", filters.keySet());
    }

    public CharFilter get(String name) {
        log.debug("Retrieving char filter: {}", name);
        return cache.computeIfAbsent(name, n -> {
            Supplier<CharFilter> supplier = filters.get(n);
            if (supplier == null) {
                log.warn("Unknown char filter requested: {}", n);
                throw new Exceptions.AnalyzerNotFoundException("Unknown char filter: " + n);}
            return supplier.get();
        });

    }
}