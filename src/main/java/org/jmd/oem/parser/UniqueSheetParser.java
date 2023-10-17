package org.jmd.oem.parser;

public abstract class UniqueSheetParser<T> implements SheetParser<T> {
    public String getSheetName() {
        return null;
    }
}
