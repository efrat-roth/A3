package org.analyzing;

import org.analyzing.charFilters.CharFilter;
import org.analyzing.tokenFilters.TokenFilter;
import org.analyzing.tokenizers.Tokenizer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
@Builder
public class Analyzer {
    private List<CharFilter> charFilters;
    private Tokenizer tokenizer;
    private List<TokenFilter> tokenFilters;

    public List<Token> analyze(String input) throws IOException {
        log.debug("Starting analysis: inputLength {}, charFilterCount {}, tokenFilterCount {}",
                input.length(), charFilters.size(), tokenFilters.size());
        for (CharFilter charFilter : charFilters) {
            log.debug("Applying char filter: {}", charFilter.getClass().getSimpleName());
            input = charFilter.apply(input);
            log.debug("Char filter applied: {}, outputLength {}", charFilter.getClass().getSimpleName(), input.length());
        }
        log.debug("Tokenizing input: tokenizer {}, inputLength {}", tokenizer.getClass().getSimpleName(), input.length());
        List<Token> tokensOfInput = tokenizer.tokenize(input);
        log.debug("Tokenization completed: tokenizer {}, tokenCount {}", tokenizer.getClass().getSimpleName(), tokensOfInput.size());
        for (TokenFilter tokenFilter : tokenFilters) {
            log.debug("Applying token filter: {}, inputTokenCount {}", tokenFilter.getClass().getSimpleName(), tokensOfInput.size());
            tokensOfInput = tokenFilter.apply(tokensOfInput);
            log.debug("Token filter applied: {}, outputTokenCount {}", tokenFilter.getClass().getSimpleName(), tokensOfInput.size());
        }
        log.info("Analysis completed: tokenCount {}", tokensOfInput.size());
        return tokensOfInput;
    }
}
