package org.utils.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig {
    public StorageConfig storage;
    public AnalyzerConfig analyzer;
    public IndexConfig index;
}
