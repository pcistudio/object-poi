package com.pcistudio.poi.processor;


import java.io.*;
import java.nio.file.Path;
import java.util.Map;

/**
 * Write the whole content in the excel document at once
 */
public interface WorkbookWriter {
    /**
     *  Write the whole map in the excel document
     * @param path expected file path to store the map data
     * @param sheetsData each key in the map represent the sheetName and the value the object to store in that sheet
     * @throws IOException
     */
    void write(Path path, Map<String, Object> sheetsData) throws IOException;

    /**
     *  Write the whole object in the excel document
     * @param outputStream to store the object data
     * @param sheetsData each property in the object represent the sheetName and the value the object to store in that sheet
     * @throws IOException
     */
    void write(OutputStream outputStream, Object sheetsData) throws IOException;

    /**
     *  Write the whole object in the excel document
     * @param path to store the object data
     * @param sheetsData each property in the object represent the sheetName and the value the object to store in that sheet
     * @throws IOException
     */
    default void write(Path path, Object sheetsData) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(path.toFile())) {
            write(outputStream, sheetsData);
        }
    }
}