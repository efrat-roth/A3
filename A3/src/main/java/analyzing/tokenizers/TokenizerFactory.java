package analyzing.tokenizers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TokenizerFactory {
    private final Map<String, Supplier<Tokenizer>> filters =
            new HashMap<>();

    public TokenizerFactory() {
        filters.put("whitespace", WhitespaceTokenizer::new);
    }

    public Tokenizer get(String name) {
        return filters.get(name).get();
    }
}
