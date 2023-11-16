package com.pcistudio.poi.parser;

import java.util.List;

public class TableSectionParserBuilder<T> {
    private String name;
    private List<T> objectToBuild;

    private int rowStartIndex = -1;

    private int columnStartIndex = 0;

    private String startValue;

    private Class<T> recordClass;

    //TODO it doesn't look look like it make sence bc the property list for the object already have a columnCount
    // in the pivot it makes sence
    private Short columnCount;

    private boolean displayNextRow = true;

    public TableSectionParserBuilder<T> withName(String name) {
        this.name = name;
        return this;
    }

    public TableSectionParserBuilder<T> withObjectToBuild(List<T> objectToBuild) {
        this.objectToBuild = objectToBuild;
        return this;
    }



    public TableSectionParserBuilder<T> withRowStartIndex(int rowStartIndex) {
        this.rowStartIndex = rowStartIndex;
        return this;
    }

    public TableSectionParserBuilder<T> withColumnStartIndex(int columnStartIndex) {
        this.columnStartIndex = columnStartIndex;
        return this;
    }

    public TableSectionParserBuilder<T> withStartValue(String startValue) {
        this.startValue = startValue;
        return this;
    }

    public TableSectionParserBuilder<T> displayInCurrentRow() {
        this.displayNextRow = false;
        return this;
    }

    public TableSectionParserBuilder<T> withRecordClass(Class<T> recordClass) {
        this.recordClass = recordClass;
        return this;
    }

    public TableSectionParserBuilder<T> withColumnCount(short columnCount) {
        if(columnCount <= 0) {
            throw new IllegalArgumentException("columnCount min value is 1");
        }
        this.columnCount = columnCount;
        return this;
    }


    public TableSectionParser<T> build() {
        return new TableSectionParser<>(name,
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