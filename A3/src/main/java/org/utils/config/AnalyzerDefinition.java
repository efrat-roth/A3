package org.utils.config;

import lombok.Data;

import java.util.List;

@Data
public class AnalyzerDefinition {
    private List<String> charFilters;
    private String tokenizer;
    private List<String> tokenFilters;
}