package org.analyzing.analyzerStrategy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.analyzing.Analyzer;
import org.analyzing.charFilters.CharFilterRegistry;
import org.analyzing.tokenFilters.TokenFilterRegistry;
import org.analyzing.tokenizers.TokenizerFactory;
import org.utils.Exceptions;
import org.utils.config.AnalyzerDefinition;
import org.utils.config.AppConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AnalyzerStrategyFactory {

    private final CharFilterRegistry charFilterRegistry = new CharFilterRegistry();
    private final TokenFilterRegistry tokenFilterRegistry;
    private final TokenizerFactory tokenizerFactory = new TokenizerFactory();
    @Getter
    private AnalyzerStrategy analyzerStrategy;

    public AnalyzerStrategyFactory(AppConfig config) throws IOException {
        this.tokenFilterRegistry = new TokenFilterRegistry(config);
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
            }

            default -> throw new Exceptions.UnsupportedAnalyzerStrategyException(
                    "Unknown analyzer strategy: " + config.analyzer.getStrategy());
        }
    }

    private Analyzer buildAnalyzer(AnalyzerDefinition analyzerDefinition) throws IOException {
        return Analyzer.builder().
                charFilters(charFilterRegistry.get(analyzerDefinition.getCharFilters())).
                tokenFilters(tokenFilterRegistry.get(analyzerDefinition.getTokenFilters())).
                tokenizer(tokenizerFactory.get(analyzerDefinition.getTokenizer()))
                .build();
    }
}