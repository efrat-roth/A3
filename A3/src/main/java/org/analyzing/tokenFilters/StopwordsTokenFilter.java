package org.analyzing.tokenFilters;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Token;
import org.utils.Exceptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
public class StopwordsTokenFilter implements TokenFilter {

    private final Set<String> stopwords;

    public StopwordsTokenFilter(Set<String> stopwords) {

        if (stopwords == null) {
            throw new Exceptions.AnalyzerConfigurationException("Stopwords set cannot be null");
        }

        this.stopwords = new HashSet<>(stopwords);
    }

    @Override
    public List<Token> apply(List<Token> tokens) {

        if (tokens == null) {
            throw new Exceptions.InvalidDocumentException("Token list cannot be null");
        }

        log.debug("Applying stopwords token filter: inputTokenCount {}",tokens.size());

        List<Token> filteredTokens = new ArrayList<>(tokens.size());

        for (Token token : tokens) {
            if (!stopwords.contains(token.term())) {
                filteredTokens.add(token);
            }
        }

        log.debug("Stopwords filter applied: removed {} tokens",tokens.size() - filteredTokens.size());

        return filteredTokens;
    }
}