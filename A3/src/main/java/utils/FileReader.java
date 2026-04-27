package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileReader {
    public List<String> readFileLines(String fileName) throws IOException {
        //קריאה מקובץ קונפיגורציה של הpath לפי שם הקובץ
        return Files.readAllLines(Path.of(fileName));

    }
}
