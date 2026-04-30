package org.analyzing.tokenizers;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class TokenizerFactory {
    private final Map<String, Supplier<Tokenizer>> filters =
            new HashMap<>();

    public TokenizerFactory() {
        filters.put("whitespace", WhitespaceTokenizer::new);
        log.debug("Registered tokenizers: {}", filters.keySet());
    }

    public Tokenizer get(String name) {
        log.debug("Retrieving tokenizer: {}", name);
        Supplier<Tokenizer> supplier = filters.get(name);
        if (supplier == null) {
            log.warn("Unknown tokenizer requested: {}", name);
        }
        Tokenizer tokenizer = supplier.get();
        log.debug("Tokenizer created: {}", name);
        return tokenizer;
    }
}
