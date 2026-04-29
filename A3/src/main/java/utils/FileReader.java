package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileReader {
    public static List<String> readFileLines(String fileName) throws IOException {
        return Files.readAllLines(Path.of(fileName));
    }
}
