package org.analyzing.tokenizers;

import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
public class TokenizerFactory {

    private final Map<String, Supplier<Tokenizer>> tokenizers = new HashMap<>();
    private final Map<String, Tokenizer> cache = new ConcurrentHashMap<>();


    public TokenizerFactory() {
        tokenizers.put("whitespace", WhitespaceTokenizer::new);
        log.debug("Registered tokenizers: {}", tokenizers.keySet());
    }

    public Tokenizer get(String name) {
        log.debug("Retrieving tokenizer: {}", name);
        return cache.computeIfAbsent(name, n -> {
            Supplier<Tokenizer> supplier = tokenizers.get(n);
            if (supplier == null) {
                log.warn("Unknown tokenizer requested: {}", n);
                throw new Exceptions.AnalyzerNotFoundException("Unknown tokenizer: " + n);
            }
            return supplier.get();
        });

    }
}