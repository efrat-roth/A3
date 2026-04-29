package utils.config;

import lombok.Data;

import java.util.Map;

@Data
public class AnalyzerConfig {
    private String strategy;
    private AnalyzerDefinition defaultConfig;
    private Map<String, AnalyzerDefinition> fields;
}