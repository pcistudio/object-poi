package com.pcistudio.poi.util;

import org.apache.poi.ss.usermodel.Cell;

import java.text.DecimalFormat;

public class PoiUtil {
    public static String cellString(Cell cell, String numberFormat) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                DecimalFormat df = new DecimalFormat(numberFormat);
                return df.format(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
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
}
