package org.analyzing.charFilters;

import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

import java.util.Set;

@Slf4j
public class PunctuationCharFilter implements CharFilter {

    private static final Set<Character> DEFAULT_PUNCTUATIONS =
            Set.of('.', ',', ';', ':', '!', '?', '"', '\'', '@', '#', '$', '%', '^', '&', '*', '(', ')');
    private final boolean[] lookup = new boolean[256];

    public PunctuationCharFilter() {
        for (char c : DEFAULT_PUNCTUATIONS) {
            lookup[c] = true;
        }

    }

    public PunctuationCharFilter(Set<Character> punctuations) {
        if (punctuations == null || punctuations.isEmpty()) {
            throw new Exceptions.AnalyzerConfigurationException("Punctuation set cannot be null or empty");
        }
        for (char c : punctuations) {
            lookup[c] = true;
        }
    }

    @Override
    public String apply(String input) {

        if (input == null) {
            throw new Exceptions.InvalidDocumentException("Input cannot be null");
        }

        log.debug("Applying punctuation char filter: inputLength {}", input.length());

        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (c < 256 && !lookup[c]) {
                sb.append(c);
            }
        }

        log.debug("Punctuation filter applied: outputLength {}", sb.length());

        return sb.toString();
    }
}