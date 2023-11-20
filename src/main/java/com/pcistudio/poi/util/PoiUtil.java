package com.pcistudio.poi.util;

import com.pcistudio.poi.parser.FieldDescriptor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class PoiUtil {
    private static final Logger LOG = LoggerFactory.getLogger(PoiUtil.class);

    public static String cellString(Cell cell, String numberFormat) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
//            case FORMULA:
                DecimalFormat df = new DecimalFormat(numberFormat);
                return df.format(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                DecimalFormat df1 = new DecimalFormat(numberFormat);
                return df1.format(cell.getNumericCellValue());
            case ERROR:
                return String.valueOf(cell.getErrorCellValue());
            default:
                return null;
        }
    }

    public static String cellStringTrim(Cell cell, String numberFormat) {
        String data = cellString(cell, numberFormat);
        return data == null || data.isBlank()
                ? null
                : data.trim();
    }

    public static String cellStringTrim(Cell cell) {
        return cellStringTrim(cell, "#");
    }

    public static String cellSanitize(Cell cell, String numberFormat) {
        String cellValue = cellStringTrim(cell, numberFormat);
        return cellValue == null ? null : cellValue.replaceAll("\n", "");
    }

    public static void dumpSheet(Path path, String sheetName) throws IOException {
        try (FileInputStream file = new FileInputStream(path.toFile())) {
            dumpSheet(file, sheetName);
        }
    }

    public static void dumpSheet(InputStream inputStream, String sheetName) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {

            if (workbook.getNumberOfSheets() == 0) {
                throw new IllegalStateException("Excel doesn't have any sheet ");
            }

            Sheet sheet = workbook.getSheet(sheetName);
            Objects.requireNonNull(sheet, String.format("Sheet=%s doesn't exist", sheetName));

            for (int index = 0; index <= sheet.getLastRowNum(); index++) {
                Row row = sheet.getRow(index);
                String rowdata = buildRow(row);
                LOG.debug("{}. | {}", index + 1, rowdata);
            }
        }
    }

    private static String buildRow(Row row) {
        if (row == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.getLastCellNum() ; i++) {
            Cell cell = row.getCell(i);
            sb.append(cellSanitize(cell, "#.##"));
            sb.append("\t|\t");
        }
        return sb.toString();
    }

    public static <T> T create(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error creating object from class " + clazz.getName() + ". Check for default constructor", e);
        }
    }

    public static void fillCell(Cell cell, FieldDescriptor fieldDescriptor, Object obj) {
        // For formula columns  pre-calculate before calling the write to excel
        Objects.requireNonNull(obj, "object cannot by null");
        Class<?> type = fieldDescriptor.getFieldWrapType();

        Object fieldValue = retrieveFieldValue(fieldDescriptor.getField(), obj);
        if (fieldValue == null) {
            return;
        }

        if (Number.class.isAssignableFrom(type)) {
            cell.setCellValue(((Number) fieldValue).doubleValue());
        } else if (Date.class.isAssignableFrom(type)) {
            cell.setCellValue((Date) fieldValue);
        } else if (LocalDateTime.class.isAssignableFrom(type)) {
            cell.setCellValue((LocalDateTime) fieldValue);
        } else if (LocalDate.class.isAssignableFrom(type)) {
            cell.setCellValue((LocalDate) fieldValue);
        } else if (Calendar.class.isAssignableFrom(type)) {
            cell.setCellValue((Calendar) fieldValue);
        } else {
            cell.setCellValue(String.valueOf(fieldValue));
        }
    }


    public static Object retrieveFieldValue(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Error getting field=%s from class=%s", field.getName(), obj.getClass().getCanonicalName()), e);
        }
    }

    public static Object retrieveFieldValue(FieldDescriptor descriptor, Object obj) {
        return retrieveFieldValue(descriptor.getField(), obj);
    }
}
