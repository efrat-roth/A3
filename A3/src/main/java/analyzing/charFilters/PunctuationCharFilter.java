package analyzing.charFilters;

import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class PunctuationCharFilter implements CharFilter {
    Set<Character> punctuations = new HashSet<Character>();

    @Override
    public String apply(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (!punctuations.contains(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
