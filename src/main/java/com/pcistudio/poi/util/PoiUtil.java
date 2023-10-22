package com.pcistudio.poi.util;

import com.pcistudio.poi.parser.SheetParser;
import com.pcistudio.poi.processor.WorkbookProcessor;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Map;
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
        try (FileInputStream file = new FileInputStream(path.toFile());
             Workbook workbook = new XSSFWorkbook(file)) {

            if (workbook.getNumberOfSheets() == 0) {
                throw new IllegalStateException(String.format("Excel file=%s doesn't have any sheet ", path));
            }

            Sheet sheet = workbook.getSheet(sheetName);
            Objects.requireNonNull(sheet, String.format("Sheet=%s doesn't exist", sheetName));

            for (int index = 0; index <= sheet.getLastRowNum(); index++) {
                Row row = sheet.getRow(index);
                String rowdata = buildRow(row);
                LOG.debug("{}. | {}", index+1, rowdata);
            }

        }
    }

    private static String buildRow(Row row) {
        if (row == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Cell cell: row) {
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
}
