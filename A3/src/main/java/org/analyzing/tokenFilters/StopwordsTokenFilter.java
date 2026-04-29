package org.analyzing.tokenFilters;

import org.analyzing.Token;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.utils.ConfigLoader;
import org.utils.config.AppConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.utils.FileReader.readFileLines;

@NoArgsConstructor
@AllArgsConstructor
public class StopwordsTokenFilter implements TokenFilter {
    private Set<String> stopwords = new HashSet<>();

    @Override
    public List<Token> apply(List<Token> tokens) throws IOException {
        if (stopwords.isEmpty()) {
            stopwords = readStopwords();
        }
        tokens = tokens.stream().filter(token -> !stopwords.contains(token.term())).collect(Collectors.toList());
        return tokens;
    }

    //To implement after readerFile class
    public Set<String> readStopwords() throws IOException {

        AppConfig config = ConfigLoader.load();
        return new HashSet<>(readFileLines(config.storageConfig.getStopwordsFilePath()));
    }

}

