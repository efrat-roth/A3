package org.analyzing.charFilters;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LowercaseCharFilter implements CharFilter {

    @Override
    public String apply(String input) {
        input = input.toLowerCase();
        return input;
    }
}
