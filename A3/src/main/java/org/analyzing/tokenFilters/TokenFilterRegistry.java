package org.analyzing.tokenFilters;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class TokenFilterRegistry {

    private final Map<String, Supplier<TokenFilter>> filters =
            new HashMap<>();

    public TokenFilterRegistry() {
        filters.put("stopwords", StopwordsTokenFilter::new);
        log.debug("Registered token filters: {}", filters.keySet());
    }

    public TokenFilter get(String name) {
        log.debug("Retrieving token filter: {}", name);
        Supplier<TokenFilter> supplier = filters.get(name);
        if (supplier == null) {
            log.warn("Unknown token filter requested: {}", name);
        }
        TokenFilter tokenFilter = supplier.get();
        log.debug("Token filter created: {}", name);
        return tokenFilter;
    }
}
