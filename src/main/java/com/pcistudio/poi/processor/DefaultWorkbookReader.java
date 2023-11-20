package com.pcistudio.poi.processor;


import com.pcistudio.poi.parser.SheetParser;
import com.pcistudio.poi.util.PoiUtil;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;

public class DefaultWorkbookReader extends WorkbookProcessor implements WorkbookReader {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorkbookReader.class);

    public DefaultWorkbookReader(SheetParser<?>... sheetParsers) {
        super(sheetParsers);
    }

    public DefaultWorkbookReader(List<SheetParser<?>> list) {
        super(list);
    }

    public DefaultWorkbookReader(boolean failIfSheetNotFound, List<SheetParser<?>> list) {
        super(failIfSheetNotFound, list);
    }


    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public Map<String, Object> parseToMap(Path path) throws IOException {

        try (FileInputStream file = new FileInputStream(path.toFile());
             Workbook workbook = new XSSFWorkbook(file)) {
            Map<String, Object> map = new HashedMap<>();
            if (workbook.getNumberOfSheets() == 0) {
                throw new IllegalStateException(String.format("Excel file=%s doesn't have any sheet ", path));
            }
            for (SheetParser<?> sheetParser : sheetParsers) {
                Object sheetResult = parseSheet(workbook, sheetParser);
                map.put(sheetParser.getSheetName(), sheetResult);
            }
            return map;
        }
    }


    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public <T> T parseToObject(InputStream inputStream, Class<T> resultClass) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            T result = PoiUtil.create(resultClass);
            Map<Class<?>, Field> fieldMap = loadSheetFields(resultClass);
            checkAllSheetsHasField(fieldMap);

            if (workbook.getNumberOfSheets() == 0) {
                throw new IllegalStateException("Excel doesn't have any sheet");
            }
            for (SheetParser<?> sheetParser : sheetParsers) {
                Object sheetResult = parseSheet(workbook, sheetParser);
                Field field = fieldMap.get(sheetResult.getClass());
                set(field, result, sheetResult);
            }
            return result;
        }
    }

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    private static <T> void set(Field field, T result, Object sheetResult) {
        try {
            field.setAccessible(true);
            field.set(result, sheetResult);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
    }



    private Object parseSheet(Workbook workbook, SheetParser<?> sheetParser) {
        Sheet sheet = sheetParser.getSheetName() == null
                ? workbook.getSheetAt(0)
                : workbook.getSheet(sheetParser.getSheetName());
        if (sheet == null) {
            handleSheetNotFound(sheetParser.getSheetName());
        }
        return sheetParser.parse(sheet);
    }

}
