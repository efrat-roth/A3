package org.utils.config;

import lombok.Data;

import java.util.Map;

@Data
public class AnalyzerConfig {
    private String strategy;
    private AnalyzerDefinition analyzerDefinition;
    private Map<String, AnalyzerDefinition> fields;
}