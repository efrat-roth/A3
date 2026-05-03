package org.analyzing;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.charFilters.CharFilter;
import org.analyzing.tokenFilters.TokenFilter;
import org.analyzing.tokenizers.Tokenizer;
import org.utils.Exceptions;

import java.io.IOException;
import java.util.List;

@Slf4j
@Builder
public class Analyzer {

    private List<CharFilter> charFilters;
    private Tokenizer tokenizer;
    private List<TokenFilter> tokenFilters;

    public List<Token> analyze(String input) throws IOException {
        validate(input);
        log.debug("Starting analysis: inputLength {}, charFilterCount {}, tokenFilterCount {}",
                input.length(), charFilters.size(), tokenFilters.size());

        for (CharFilter charFilter : charFilters) {
            input = charFilter.apply(input);
        }

        List<Token> tokens = tokenizer.tokenize(input);

        for (TokenFilter tokenFilter : tokenFilters) {
            tokens = tokenFilter.apply(tokens);
        }

        log.info("Analysis completed: tokenCount {}", tokens.size());

        return tokens;
    }

    private void validate(String input) {

        if (input == null) {
            throw new Exceptions.InvalidDocumentException("Input text cannot be null");
        }

        if (tokenizer == null) {
            throw new Exceptions.AnalyzerConfigurationException("Tokenizer is not configured");
        }

        if (charFilters == null) {
            throw new Exceptions.AnalyzerConfigurationException("Char filters are not configured");
        }

        if (tokenFilters == null) {
            throw new Exceptions.AnalyzerConfigurationException("Token filters are not configured");
        }
    }
}