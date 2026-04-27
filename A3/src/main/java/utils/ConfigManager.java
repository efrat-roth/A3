package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

public class ConfigManager {

    public static void load(String fileName, String propertyName) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream input =
                    ConfigLoader.class.getClassLoader()
                            .getResourceAsStream(fileName);

            return mapper.readValue(input, AnalyzerConfig.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }
}