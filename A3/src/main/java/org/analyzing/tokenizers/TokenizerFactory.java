package org.analyzing.tokenizers;

import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class TokenizerFactory {

    private final Map<String, Supplier<Tokenizer>> tokenizers = new HashMap<>();

    public TokenizerFactory() {
        tokenizers.put("whitespace", WhitespaceTokenizer::new);
        log.debug("Registered tokenizers: {}", tokenizers.keySet());
    }

    public Tokenizer get(String name) {

        if (name == null || name.isBlank()) {
            throw new Exceptions.AnalyzerConfigurationException("Tokenizer name cannot be null or blank");
        }

        log.debug("Retrieving tokenizer: {}", name);

        Supplier<Tokenizer> supplier = tokenizers.get(name);

        if (supplier == null) {
            log.warn("Unknown tokenizer requested: {}", name);

            throw new Exceptions.AnalyzerNotFoundException("Unknown tokenizer: " + name );
        }

        Tokenizer tokenizer = supplier.get();
        log.debug("Tokenizer created: {}", name);
        return tokenizer;
    }
}