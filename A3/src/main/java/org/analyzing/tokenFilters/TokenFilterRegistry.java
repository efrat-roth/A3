package org.analyzing.tokenFilters;

import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
public class TokenFilterRegistry {

    private final Map<String, Supplier<TokenFilter>> filters = new HashMap<>();
    private final Map<String, TokenFilter> cache = new ConcurrentHashMap<>();

    public TokenFilterRegistry(Set<String> stopwords) {

        filters.put("stopwords", () -> new StopwordsTokenFilter(stopwords));
        log.debug("Registered token filters: {}", filters.keySet());
    }

    public TokenFilter get(String name) {
        log.debug("Retrieving token filter: {}", name);
        return cache.computeIfAbsent(name, n -> {
            Supplier<TokenFilter> tokenFilter = filters.get(n);
            if (tokenFilter == null) {
                log.warn("Unknown token filter requested: {}", n);
                throw new Exceptions.AnalyzerNotFoundException("Unknown token filter: " + n);
            }
            return tokenFilter.get();
        });
    }
}