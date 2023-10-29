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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkbookProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(WorkbookProcessor.class);

    private final List<SheetParser<?>> sheetParsers;

    private boolean failIfSheetNotFound;

    public WorkbookProcessor(SheetParser<?>... sheetParsers) {
        this(true, List.of(sheetParsers));
    }

    public WorkbookProcessor(List<SheetParser<?>> list) {
        this(true, new ArrayList<>(list));
    }

    public WorkbookProcessor(boolean failIfSheetNotFound, List<SheetParser<?>> list) {
        sheetParsers = new ArrayList<>(list);
        this.failIfSheetNotFound = failIfSheetNotFound;
    }

    public WorkbookProcessor(boolean failIfSheetNotFound, SheetParser<?>... sheetParsers) {
        this(failIfSheetNotFound, List.of(sheetParsers));
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


    public <T> T parseToObject(Path path, Class<T> resultClass) throws IOException {
        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            return parseToObject(inputStream, resultClass);
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

    private void checkAllSheetsHasField(Map<Class<?>, Field> fieldMap) {
        for (SheetParser<?> sheetParser : sheetParsers) {
            if (!fieldMap.containsKey(sheetParser.sheetClass())) {
                throw new IllegalStateException(String.format("Field for class=%s not present", sheetParser.sheetClass()));
            }
        }
    }

    private Map<Class<?>, Field> loadSheetFields(Class<?> resultClass) {
        return Arrays.stream(resultClass.getDeclaredFields())
                .collect(Collectors.toMap(Field::getType, Function.identity()));
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

    private void handleSheetNotFound(String sheetName) {
        if (failIfSheetNotFound) {
            throw new IllegalArgumentException(String.format("Sheet=%s doesn't exist", sheetName));
        } else {
            LOG.warn("Sheet={} doesn't exist", sheetName);
        }
    }

}
