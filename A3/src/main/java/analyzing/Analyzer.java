package analyzing;

import lombok.Builder;

import java.util.List;

@Builder
public class Analyzer {
    private List<CharFilter> charFilters;
    private Tokenizer tokenizer;
    private List<TokenFilter> tokenFilters;

    public List<Token> analyze(String input) {
        for (CharFilter charFilter : charFilters) {
            input = charFilter.apply(input);
        }
        List<Token> tokensOfInput = tokenizer.tokenize(input);
        for (TokenFilter tokenFilter : tokenFilters) {
            tokensOfInput = tokenFilter.apply(tokensOfInput);
        }
        return tokensOfInput;
    }
}
