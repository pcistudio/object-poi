package com.pcistudio.poi.parser;

public class SheetCursor {
    private SectionDescriptor<?> sectionDescriptor;
    private int sectionStartRow = 0;
//    private int sectionStartCol = 0;
    private int nextRow = 0;
    private int nextCol = 0;

    public SheetCursor() {
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

    public void beginSection(SectionDescriptor<?> sectionDescriptor) {
        this.sectionDescriptor = sectionDescriptor;
        if (willOverrideData()) {
            throw new IllegalStateException(String.format("About to override row %s with sheet %s. " +
                    "Check that previous section is not bigger than expected. " +
                    "For dynamic size better use startName property", sectionDescriptor.getRowStartIndex()
                    , "sheet.getSheetName()"));
//                    , sheet.getSheetName()));
        }
        if (sectionDescriptor.isDisplayNextRow()) {
            nextCol = sectionDescriptor.getColumnStartIndex();
            nextRow = sectionDescriptor.isStartIndexNotSet() ? nextRow : sectionDescriptor.getRowStartIndex();
            sectionStartRow = nextRow;
        } else {

            nextRow = sectionStartRow;
        }
    }
}
