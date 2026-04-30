package org.analyzing.charFilters;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class LowercaseCharFilter implements CharFilter {

    @Override
    public String apply(String input) {
        log.debug("Applying lowercase char filter: inputLength {}", input.length());
        input = input.toLowerCase();
        log.debug("Lowercase char filter applied: outputLength {}", input.length());
        return input;
    }
}
