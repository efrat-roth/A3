package org.analyzing.charFilters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LowercaseCharFilter implements CharFilter {

    @Override
    public String apply(String input) {
        log.debug("Applying lowercase char filter: inputLength {}", input.length());
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            sb.append(Character.toLowerCase(input.charAt(i)));
        }
        log.debug("Lowercase char filter applied: outputLength {}", sb.length());
        return sb.toString();
    }
}