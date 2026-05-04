package org.analyzing.charFilters;

import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class PunctuationCharFilter implements CharFilter {

    private static final Set<Character> DEFAULT_PUNCTUATIONS =
            Set.of('.', ',', ';', ':', '!', '?', '"', '\'', '@', '#', '$', '%', '^', '&', '*', '(', ')');
    private final Set<Character> punctuations;

    public PunctuationCharFilter() {
        this.punctuations = new HashSet<>(DEFAULT_PUNCTUATIONS);
    }

    public PunctuationCharFilter(Set<Character> punctuations) {
        if (punctuations == null || punctuations.isEmpty()) {
            throw new Exceptions.AnalyzerConfigurationException("Punctuation set cannot be null or empty");
        }

        this.punctuations = new HashSet<>(punctuations);
    }

    @Override
    public String apply(String input) {

        if (input == null) {
            throw new Exceptions.InvalidDocumentException("Input cannot be null");
        }

        log.debug("Applying punctuation char filter: inputLength {}, punctuationCount {}"
                ,input.length(),punctuations.size());

        StringBuilder sb = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (!punctuations.contains(c)) {
                sb.append(c);
            }
        }

        String output = sb.toString();

        log.debug("Punctuation filter applied: outputLength {}",output.length());

        return output;
    }
}