package org.analyzing.tokenFilters;

import org.analyzing.Token;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.utils.ConfigLoader;
import org.utils.config.AppConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.utils.FileReader.readFileLines;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class StopwordsTokenFilter implements TokenFilter {
    private Set<String> stopwords = new HashSet<>();

    @Override
    public List<Token> apply(List<Token> tokens) throws IOException {
        log.debug("Applying stopwords token filter: inputTokenCount {}", tokens.size());
        if (stopwords.isEmpty()) {
            log.debug("Stopwords set is empty, loading stopwords");
            stopwords = readStopwords();
            log.debug("Stopwords loaded: count {}", stopwords.size());
        }
        List<Token> filteredTokens = tokens.stream().filter(token -> !stopwords.contains(token.term())).collect(Collectors.toList());
        log.debug("Stopwords token filter applied: inputTokenCount {}, outputTokenCount {}, removedTokenCount {}",
                tokens.size(), filteredTokens.size(), tokens.size() - filteredTokens.size());
        return filteredTokens;
    }

    //To implement after readerFile class
    public Set<String> readStopwords() throws IOException {

        AppConfig config = ConfigLoader.load();
        String stopwordsFilePath = config.storageConfig.getStopwordsFilePath();
        log.debug("Reading stopwords file: path {}", stopwordsFilePath);
        Set<String> loadedStopwords = new HashSet<>(readFileLines(stopwordsFilePath));
        log.debug("Stopwords file read: path {}, count {}", stopwordsFilePath, loadedStopwords.size());
        return loadedStopwords;
    }

}

