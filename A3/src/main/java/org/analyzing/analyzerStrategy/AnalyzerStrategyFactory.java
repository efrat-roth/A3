package org.analyzing.analyzerStrategy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Analyzer;
import org.analyzing.charFilters.CharFilterProvider;
import org.analyzing.tokenFilters.TokenFilterProvider;
import org.analyzing.tokenizers.TokenizerProvider;
import org.utils.Exceptions;
import org.utils.config.AnalyzerDefinition;
import org.utils.config.AppConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AnalyzerStrategyFactory {

    private final CharFilterProvider charFilterProvider = new CharFilterProvider();
    private final TokenFilterProvider tokenFilterProvider;
    private final TokenizerProvider tokenizerProvider = new TokenizerProvider();
    @Getter
    private AnalyzerStrategy analyzerStrategy;

    public AnalyzerStrategyFactory(AppConfig config) throws IOException {
        this.tokenFilterProvider = new TokenFilterProvider(config);
        registerAnalyzers(config);
    }

    private void registerAnalyzers(AppConfig config) throws IOException {

        switch (config.analyzer.getStrategy()) {
            case "default" -> {
                AnalyzerDefinition analyzerDefinition = config.analyzer.getAnalyzerDefinition();
                analyzerStrategy = new DefaultAnalyzerStrategy(buildAnalyzer(analyzerDefinition));
            }
            case "fieldBased" -> {
                Map<String, Analyzer> fieldAnalyzers = new HashMap<>();
                for (Map.Entry<String, AnalyzerDefinition> entry : config.analyzer.getFields().entrySet()) {
                    fieldAnalyzers.put(entry.getKey(), buildAnalyzer(entry.getValue()));
                }
                analyzerStrategy = new FieldBasedAnalyzerStrategy(fieldAnalyzers);
                ;
            }

            default -> throw new Exceptions.UnsupportedAnalyzerStrategyException(
                    "Unknown analyzer strategy: " + config.analyzer.getStrategy());
        }
    }

    private Analyzer buildAnalyzer(AnalyzerDefinition analyzerDefinition) throws IOException {
        return Analyzer.builder().
                charFilters(charFilterProvider.provide(analyzerDefinition.getCharFilters())).
                tokenFilters(tokenFilterProvider.provide(analyzerDefinition.getTokenFilters())).
                tokenizer(tokenizerProvider.provide(analyzerDefinition.getTokenizer()))
                .build();
    }
}