package com.pcistudio.poi.parser;


import java.util.Optional;

public interface SectionBox extends SectionLocation {

    int getRowCount();

    /**
     * getColumnCount is always referring to the Excel columns
     *
     * @return
     */
    short getColumnCount();

    default Optional<Integer> nextSectionRowIndex() {
        if (isStartIndexSet()) {
            return Optional.of(getRowStartIndex() + getRowCount());
        } else {
            return Optional.empty();
        }
    }

    default int nextSectionColIndex() {
        return getColumnStartIndex() + getColumnCount();
    }

    boolean isDisplayNextRow();

    default boolean isDisplaySameRow() {
        return !isDisplayNextRow();
    }

}