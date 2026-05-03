package org.analyzing.analyzerStrategy;

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

public class AnalyzerProvider {

    private final AppConfig config;
    private final CharFilterProvider charFilterProvider;
    private final TokenFilterProvider tokenFilterProvider;
    private final TokenizerProvider tokenizerProvider;

    public AnalyzerProvider(AppConfig config) throws IOException {
        this.config = config;
        this.charFilterProvider = new CharFilterProvider();
        this.tokenFilterProvider = new TokenFilterProvider(config);
        this.tokenizerProvider = new TokenizerProvider();
    }

    public AnalyzerStrategy provide() throws IOException {
        AnalyzerDefinition analyzerDefinition;
        if ("default".equals(config.analyzerConfig.getStrategy())) {
            analyzerDefinition = config.analyzerConfig.getDefaultConfig();
            return new DefaultAnalyzerStrategy(buildAnalyzer(analyzerDefinition));
        }

        if ("fieldBased".equals(config.analyzerConfig.getStrategy())) {
            Map<String, Analyzer> analyzersFields = new HashMap<>();
            Map<String, AnalyzerDefinition> fieldsAnalyzers = config.analyzerConfig.getFields();

            for (Map.Entry<String, AnalyzerDefinition> entry : fieldsAnalyzers.entrySet()) {
                analyzerDefinition = entry.getValue();
                analyzersFields.put(entry.getKey(), buildAnalyzer(analyzerDefinition));
            }

            return new FieldBasedAnalyzerStrategy(analyzersFields);
        }

        throw new Exceptions.UnsupportedAnalyzerStrategyException
                ("Unsupported analyzer strategy was defined in configuration: " + config.analyzerConfig.getStrategy());
    }

    private Analyzer buildAnalyzer(AnalyzerDefinition analyzerDefinition) throws IOException {
        return Analyzer.builder().
                charFilters(charFilterProvider.provide(analyzerDefinition.getCharFilters())).
                tokenFilters(tokenFilterProvider.provide(analyzerDefinition.getTokenFilters())).
                tokenizer(tokenizerProvider.provide(analyzerDefinition.getTokenizer()))
                .build();
    }
}