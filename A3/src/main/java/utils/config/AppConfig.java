package utils.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig {
    public StorageConfig storageConfig;
    public AnalyzerConfig analyzerConfig;
    public IndexConfig indexConfig;
}
