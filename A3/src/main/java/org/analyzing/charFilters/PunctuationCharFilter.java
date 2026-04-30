package org.analyzing.charFilters;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class PunctuationCharFilter implements CharFilter {
    Set<Character> punctuations = new HashSet<Character>();

    @Override
    public String apply(String input) {
        log.debug("Applying punctuation char filter: inputLength {}, punctuationCount {}", input.length(), punctuations.size());
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (!punctuations.contains(c)) {
                sb.append(c);
            }
        }
        String output = sb.toString();
        log.debug("Punctuation char filter applied: inputLength {}, outputLength {}", input.length(), output.length());
        return output;
    }
}
