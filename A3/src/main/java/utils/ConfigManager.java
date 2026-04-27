package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    public static Properties load(String fileName, String propertiesName) {
        Properties props = new Properties();

        try (InputStream input =
                     ConfigurationManager.class.getClassLoader()
                             .getResourceAsStream(fileName)) {

            if (input == null) {
                throw new IllegalArgumentException("File not found: " + fileName);
            }

            props.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load properties file", e);
        }

        return props;
    }
}
