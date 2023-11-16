package com.pcistudio.poi.parser;

import java.util.List;

public class PivotSectionParserBuilder<T> {

    private static final int MIN_PIVOT_COLUMN = 2;
    private String name;
    private List<T> objectToBuild;

    private int rowStartIndex = -1;

    private int columnStartIndex = 0;

    private Short columnCount;

    private String startValue;

    private Class<T> recordClass;

    private boolean displayNextRow = true;

    public PivotSectionParserBuilder<T> withName(String name) {
        this.name = name;
        return this;
    }

    public PivotSectionParserBuilder<T> withObjectToBuild(List<T> objectToBuild) {
        this.objectToBuild = objectToBuild;
        return this;
    }

    public PivotSectionParserBuilder<T> withStartValue(String startValue) {
        this.startValue = startValue;
        return this;
    }

    public PivotSectionParserBuilder<T> displayInCurrentRow() {
        this.displayNextRow = false;
        return this;
    }

    public PivotSectionParserBuilder<T> withRowStartIndex(int rowStartIndex) {
        this.rowStartIndex = rowStartIndex;
        return this;
    }

    public PivotSectionParserBuilder<T> withColumnStartIndex(int columnStartIndex) {
        this.columnStartIndex = columnStartIndex;
        return this;
    }

    public PivotSectionParserBuilder<T> withColumnCount(short columnCount) {
        if(columnCount < MIN_PIVOT_COLUMN) {
            throw new IllegalArgumentException("columnCount min value is 2");
        }
        this.columnCount = columnCount;
        return this;
    }

    public PivotSectionParserBuilder<T> withRecordClass(Class<T> recordClass) {
        this.recordClass = recordClass;
        return this;
    }

    public PivotSectionParserBuilder<T> keyValue() {
        return withColumnCount((short) 2);
    }

    public PivotSectionParser<T> build() {

        return new PivotSectionParser<>(name,
                objectToBuild,
                new SectionDescriptor.Builder<T>()
                        .startValue(startValue)
                        .rowStartIndex(rowStartIndex)
                        .columnStartIndex(columnStartIndex)
                        .columnCount(columnCount)
                        .recordClass(recordClass)
                        .displayNextRow(displayNextRow)
                        .descriptorMap(FieldDescriptor.loadFrom(recordClass))
                        .build()
        );
    }
}