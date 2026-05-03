package org.analyzing.tokenFilters;

import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;
import org.utils.FileReader;
import org.utils.config.AppConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Slf4j
public class TokenFilterProvider {
    private final TokenFilterRegistry tokenFilterRegistry;

    public TokenFilterProvider(AppConfig config) throws IOException {
        String stopwordsFilePath = config.storageConfig.getStopwordsFilePath();
        tokenFilterRegistry = new TokenFilterRegistry(new HashSet<>(FileReader.readFileLines(stopwordsFilePath)));
    }

    public List<TokenFilter> provide(List<String> names) {

        if (names == null) {
            throw new Exceptions.AnalyzerConfigurationException(
                    "Token filter configuration is null"
            );
        }

        List<TokenFilter> filters = new ArrayList<>();

        for (String name : names) {

            TokenFilter tokenFilter = tokenFilterRegistry.get(name);

            if (tokenFilter == null) {
                throw new Exceptions.AnalyzerNotFoundException(
                        "Unknown token filter: " + name
                );
            }

            filters.add(tokenFilter);

            log.debug("Token filter created: {}", name);
        }

        return filters;
    }
}
