package org.jmd.oem.parser;

import org.apache.poi.ss.usermodel.Sheet;

public interface SheetParser<T> {
    T parse(Sheet sheet);

    String getSheetName();

    default void checkSheetName(Sheet sheet) {
        if (!getSheetName().equalsIgnoreCase(sheet.getSheetName())) {
            throw new IllegalArgumentException("Sheet parser is for "+ getSheetName() + " and is been use for " + sheet.getSheetName());
        }
    }

    Class<T> sheetClass();
}
