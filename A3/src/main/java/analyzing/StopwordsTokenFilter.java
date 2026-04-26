package analyzing;

import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class StopwordsTokenFilter implements TokenFilter {
    private Set<String> stopwords = new HashSet<>();

    @Override
    public List<Token> apply(List<Token> tokens) {
        stopwords = readStopwords();
        tokens = tokens.stream().filter(token -> !stopwords.contains(token.term())).collect(Collectors.toList());
        return tokens;
    }

    //To implement after readerFile class
    public Set<String> readStopwords() {
        return new HashSet<>();
    }

}

