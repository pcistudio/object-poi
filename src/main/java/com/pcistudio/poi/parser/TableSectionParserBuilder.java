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

    public TableSectionParserBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public TableSectionParserBuilder<T> objectToBuild(List<T> objectToBuild) {
        this.objectToBuild = objectToBuild;
        return this;
    }

    public TableSectionParserBuilder<T> rowStartIndex(int rowStartIndex) {
        this.rowStartIndex = rowStartIndex;
        return this;
    }

    public TableSectionParserBuilder<T> columnStartIndex(int columnStartIndex) {
        this.columnStartIndex = columnStartIndex;
        return this;
    }

    public TableSectionParserBuilder<T> startName(String startName) {
        this.startName = startName;
        return this;
    }

    public TableSectionParserBuilder<T> recordClass(Class<T> recordClass) {
        this.recordClass = recordClass;
        return this;
    }

    public TableSectionParserBuilder<T> columnCount(short columnCount) {
        if(columnCount < 1) {
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
                        .objectClass(recordClass)
                        .descriptorMap(FieldDescriptor.loadFrom(recordClass))
                        .build()
        );
    }
}