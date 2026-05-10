package org.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.utils.config.AppConfig;

import java.io.File;
import java.io.IOException;


public class ConfigLoader {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static AppConfig load() throws IOException {
        return load("src/main/resources/application.yml");
    }

    public static AppConfig load(String path) throws IOException {
        return mapper.readValue(new File(path), AppConfig.class);
    }
}