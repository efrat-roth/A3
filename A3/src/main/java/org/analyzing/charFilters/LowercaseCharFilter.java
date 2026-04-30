package org.analyzing.charFilters;

import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

@Slf4j
public class LowercaseCharFilter implements CharFilter {

    @Override
    public String apply(String input) {

        if (input == null) {
            throw new Exceptions.InvalidDocumentException("Input cannot be null");
        }

        log.debug("Applying lowercase char filter: inputLength {}",input.length());

        String output = input.toLowerCase();

        log.debug("Lowercase char filter applied: outputLength {}",output.length());
        return output;
    }
}