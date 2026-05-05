package org.analyzing.tokenizers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.utils.Exceptions;


@Slf4j
@NoArgsConstructor
public class TokenizerProvider {
    private final TokenizerFactory tokenizerFactory = new TokenizerFactory();

    public Tokenizer provide(String name) {
        Tokenizer tokenizer = tokenizerFactory.get(name);
        if (tokenizer == null) {
            throw new Exceptions.AnalyzerNotFoundException("Unknown tokenizer: " + name);
        }

        log.debug("tokenizer created: {}", name);

        return tokenizer;
    }
}
