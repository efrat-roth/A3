package org.analyzing.tokenFilters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TokenFilterRegistry {

    private final Map<String, Supplier<TokenFilter>> filters =
            new HashMap<>();

    public TokenFilterRegistry() {
        filters.put("stopwords", StopwordsTokenFilter::new);
    }

    public TokenFilter get(String name) {
        return filters.get(name).get();
    }
}
