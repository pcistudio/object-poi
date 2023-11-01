package com.pcistudio.poi.processor;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

public interface WorkbookReader {

    Map<String, Object> parseToMap(Path path) throws IOException;

    <T> T parseToObject(InputStream inputStream, Class<T> resultClass) throws IOException;

    default  <T> T parseToObject(Path path, Class<T> resultClass) throws IOException {
        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            return parseToObject(inputStream, resultClass);
        }
    }
}
