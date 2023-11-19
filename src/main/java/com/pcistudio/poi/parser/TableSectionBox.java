package com.pcistudio.poi.parser;


public class TableSectionBox implements SectionBox {

    private final int rowStart;

    private final int columnStart;
    private final int rowCount;
    private final short columnCount;

    private final boolean displayNextRow;

    public TableSectionBox(SectionDescriptor<?> sectionDescriptor, int recordNumber) {
        this.rowStart = sectionDescriptor.getRowStartIndex();
        this.rowCount = recordNumber + 1;
        this.columnCount = (short) sectionDescriptor.getDescriptorMap().size();
        this.columnStart = sectionDescriptor.getColumnStartIndex();
        this.displayNextRow = sectionDescriptor.isDisplayNextRow();
    }

    @Override
    public int getRowStartIndex() {
        return rowStart;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public short getColumnCount() {
        return columnCount;
    }

    @Override
    public int getColumnStartIndex() {
        return columnStart;
    }

    @Override
    public boolean isDisplayNextRow() {
        return displayNextRow;
    }

}