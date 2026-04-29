package org.analyzing.charFilters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CharFilterRegistry {
    private final Map<String, Supplier<CharFilter>> filters =
            new HashMap<>();

    public CharFilterRegistry() {
        filters.put("lowercase", LowercaseCharFilter::new);
        filters.put("punctuation", PunctuationCharFilter::new);
    }
    public CharFilter get(String name) {
        return filters.get(name).get();
    }
}
