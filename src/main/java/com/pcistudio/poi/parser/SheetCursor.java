package com.pcistudio.poi.parser;

public class SheetCursor {
    private SectionDescriptor<?> sectionDescriptor;

    private int nextRow = 0;

    private int nextCol = 0;

    public SheetCursor() {
    }

    public void setSectionDescriptor(SectionDescriptor<?> sectionDescriptor) {
        this.sectionDescriptor = sectionDescriptor;
    }

    public int nextRow() {
        return nextRow;
    }

    public int nextCol() {
        return nextCol;
    }

    public boolean willOverrideData() {
        return sectionDescriptor.isStartIndexSet() && nextRow > sectionDescriptor.getRowStartIndex();
    }

    public int nextRowStartIndex() {
       return nextRow = sectionDescriptor.isStartIndexNotSet() ? nextRow : sectionDescriptor.getRowStartIndex();
    }

    public void increaseRowIndex() {
        increaseRowIndex(1);
    }

    public void increaseRowIndex(int count) {
        nextRow += count;
    }

    public void increaseColIndex() {
        increaseColIndex(1);
    }

    public void increaseColIndex(int count) {
        nextCol += count;
    }
}
