package org.analyzing;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.charFilters.CharFilter;
import org.analyzing.tokenFilters.TokenFilter;
import org.analyzing.tokenizers.Tokenizer;
import org.utils.Exceptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Builder
public class Analyzer {
    @NonNull
    private final List<CharFilter> charFilters;
    @NonNull
    private final Tokenizer tokenizer;
    @Builder.Default
    private final List<TokenFilter> tokenFilters= new ArrayList<>();

    public List<Token> analyze(String input) throws IOException {
        if (input == null) {
            throw new Exceptions.InvalidDocumentException("Input text cannot be null");
        }
        log.debug("Starting analysis: inputLength {}, charFilterCount {}, tokenFilterCount {}",
                input.length(), charFilters.size(), tokenFilters.size());

        for (CharFilter charFilter : charFilters) {
            input = charFilter.apply(input);
        }

        List<Token> tokens = tokenizer.tokenize(input);

        for (TokenFilter tokenFilter : tokenFilters) {
           tokenFilter.apply(tokens);
        }

        log.info("Analysis completed: tokenCount {}", tokens.size());

        return tokens;
    }

}