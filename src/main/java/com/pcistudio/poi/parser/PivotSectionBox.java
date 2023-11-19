package com.pcistudio.poi.parser;


import com.pcistudio.poi.util.Preconditions;

public class PivotSectionBox implements SectionBox {

    private final int rowStart;

    private final int columnStart;
    private final int rowCount;
    private final short columnCount;

    private final boolean displayNextRow;

    public PivotSectionBox(SectionDescriptor<?> sectionDescriptor, int recordNumber) {
        Preconditions.lessThan(recordNumber, Short.MAX_VALUE, "The number of columns for a pivot table can not be greater than Short.MAX_VALUE");
        if (sectionDescriptor.getColumnCount() != null) {
            Preconditions.greaterThan(
                    sectionDescriptor.getColumnCount(),
                    recordNumber,
                    String.format("The number of columns %s for a pivot table has to be greater than recordNumber + 1 => %s", sectionDescriptor.getColumnCount(), recordNumber));
        }
        this.rowStart = sectionDescriptor.getRowStartIndex();
        this.rowCount = sectionDescriptor.getDescriptorMap().size();
        this.columnCount = sectionDescriptor.getColumnCount() == null ? (short) (recordNumber + 1) : sectionDescriptor.getColumnCount();
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