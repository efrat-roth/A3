package org.analyzing.tokenFilters;

import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;
import org.utils.config.AppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
public class TokenFilterRegistry {

    private final Map<String, Supplier<TokenFilter>> filters = new HashMap<>();
    private final Map<String, TokenFilter> cache = new ConcurrentHashMap<>();

    public TokenFilterRegistry(AppConfig config) {
        String stopwordsFilePath = config.storage.getStopwordsFilePath();
        filters.put("stopwords", () -> {
            try {
                return new StopwordsTokenFilter(new HashSet<>(Files.readAllLines(Path.of(stopwordsFilePath))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        log.debug("Registered token filters: {}", filters.keySet());
    }

    public List<TokenFilter> get(List<String> names) {
        List<TokenFilter> requestedFilters = new ArrayList<>();
        for (String name : names) {
            log.debug("Retrieving token filter: {}", name);
            requestedFilters.add( cache.computeIfAbsent(name, n -> {
                Supplier<TokenFilter> tokenFilter = filters.get(n);
                if (tokenFilter == null) {
                    log.warn("Unknown token filter requested: {}", n);
                    throw new Exceptions.AnalyzerNotFoundException("Unknown token filter: " + n);
                }
                return tokenFilter.get();
            }));
        }
        return requestedFilters;
    }
}