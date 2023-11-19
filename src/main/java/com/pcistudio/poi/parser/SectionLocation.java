package com.pcistudio.poi.parser;

public interface SectionLocation {

    int getRowStartIndex();
    int getColumnStartIndex();

    default boolean isStartIndexNotSet() {
        return getRowStartIndex() < 0;
    }

    default boolean isStartIndexSet() {
        return !isStartIndexNotSet();
    }
}
