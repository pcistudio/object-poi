package com.pcistudio.poi.parser;

import org.apache.poi.ss.usermodel.Sheet;

public interface SheetParser<T> {
    T parse(Sheet sheet);

    void write(Sheet sheet, Object objToWrite);

    String getSheetName();

    default void checkSheetName(Sheet sheet) {
        if (!getSheetName().equalsIgnoreCase(sheet.getSheetName())) {
            throw new IllegalArgumentException("Sheet parser is for "+ getSheetName() + " and is been use for " + sheet.getSheetName());
        }
    }

    Class<T> sheetClass();
}
