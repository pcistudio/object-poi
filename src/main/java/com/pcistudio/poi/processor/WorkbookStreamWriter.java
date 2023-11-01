package com.pcistudio.poi.processor;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

/**
 * Allow partial write of objects in the excel document
 */
public interface WorkbookStreamWriter extends AutoCloseable {
    void write(Object object) throws IOException;
}
