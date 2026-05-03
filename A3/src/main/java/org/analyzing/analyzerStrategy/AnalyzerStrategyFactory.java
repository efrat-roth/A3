package org.analyzing.analyzerStrategy;

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

    private final Map<String, AnalyzerStrategy> analyzers = new HashMap<>();
    private final CharFilterProvider charFilterProvider;
    private final TokenFilterProvider tokenFilterProvider;
    private final TokenizerProvider tokenizerProvider;

    public AnalyzerStrategyFactory(AppConfig config) throws IOException {

        this.charFilterProvider = new CharFilterProvider();
        this.tokenFilterProvider = new TokenFilterProvider(config);
        this.tokenizerProvider = new TokenizerProvider();
        buildAnalyzers(config);
    }

    public AnalyzerStrategy get(String name) {

        if (name == null || name.isBlank()) {
            throw new Exceptions.UnsupportedAnalyzerStrategyException("Analyzer strategy name cannot be null or blank");
        }
        AnalyzerStrategy analyzerStrategy = analyzers.get(name);
        if (analyzerStrategy == null) {
            throw new Exceptions.UnsupportedAnalyzerStrategyException("Unknown analyzer strategy: " + name);
        }
        return analyzerStrategy;
    }

    private void buildAnalyzers(AppConfig config) throws IOException {
        AnalyzerDefinition analyzerDefinition;

        if ("default".equals(config.analyzer.getStrategy())) {
            analyzerDefinition = config.analyzer.getAnalyzerDefinition();
            analyzers.put("default", new DefaultAnalyzerStrategy(buildAnalyzer(analyzerDefinition)));
        }

        if ("fieldBased".equals(config.analyzer.getStrategy())) {
            Map<String, Analyzer> analyzersFields = new HashMap<>();
            Map<String, AnalyzerDefinition> fieldsAnalyzers = config.analyzer.getFields();
            for (Map.Entry<String, AnalyzerDefinition> entry : fieldsAnalyzers.entrySet()) {
                analyzerDefinition = entry.getValue();
                analyzersFields.put(entry.getKey(), buildAnalyzer(analyzerDefinition));
            }
            analyzers.put("fieldBased", new FieldBasedAnalyzerStrategy(analyzersFields));
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