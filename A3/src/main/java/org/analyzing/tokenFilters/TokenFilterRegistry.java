package org.analyzing.tokenFilters;

import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class TokenFilterRegistry {

    private final Map<String, TokenFilter> filters = new HashMap<>();

    public TokenFilterRegistry(Set<String> stopwords) {

        filters.put("stopwords", new StopwordsTokenFilter(stopwords));
        log.debug("Registered token filters: {}", filters.keySet());
    }

    public TokenFilter get(String name) {
        log.debug("Retrieving token filter: {}", name);
        TokenFilter filter = filters.get(name);
        if (filter == null) {
            log.warn("Unknown token filter requested: {}", name);
            throw new Exceptions.AnalyzerNotFoundException("Unknown token filter: " + name);
        }
        log.debug("Token filter created: {}", name);
        return filter;
    }
}