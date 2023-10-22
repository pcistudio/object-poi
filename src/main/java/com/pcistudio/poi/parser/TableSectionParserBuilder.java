package com.pcistudio.poi.parser;

import java.util.List;

public class TableSectionParserBuilder<T> {
    private String name;
    private List<T> objectToBuild;

    private int rowStartIndex = -1;

    private int columnStartIndex = 0;

    private String startName;

    private Class<T> recordClass;

    private Short columnCount;

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

    public TableSectionParserBuilder<T> withStartName(String startName) {
        this.startName = startName;
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
        return new TableSectionParser<T>(name,
                objectToBuild,
                new SectionParserContext.Builder<T>()
                        .startName(startName)
                        .rowStartIndex(rowStartIndex)
                        .columnStartIndex(columnStartIndex)
                        .columnCount(columnCount)
                        .recordClass(recordClass)
                        .descriptorMap(FieldDescriptor.loadFrom(recordClass))
                        .build()
        );
    }
}