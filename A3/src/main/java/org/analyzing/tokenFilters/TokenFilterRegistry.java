package org.analyzing.tokenFilters;

import org.utils.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TokenFilterRegistry {

    private final Map<String, TokenFilter> filters = new HashMap<>();

    public TokenFilterRegistry(Set<String> stopwords) {
        filters.put("stopwords", new StopwordsTokenFilter(stopwords));
    }

    public TokenFilter get(String name) {
        TokenFilter filter = filters.get(name);
        if (filter == null) {
            throw new Exceptions.AnalyzerNotFoundException("Unknown token filter: " + name);
        }
        return filter;
    }
}