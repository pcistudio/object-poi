package com.pcistudio.poi.parser;

import java.util.List;

public class PivotSectionParserBuilder<T> {
    private String name;
    private List<T> objectToBuild;

    private int rowStartIndex = -1;

    private int columnStartIndex = 0;

    private Short columnCount;

    private String startName;

    private Class<T> recordClass;

    public PivotSectionParserBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public PivotSectionParserBuilder<T> objectToBuild(List<T> objectToBuild) {
        this.objectToBuild = objectToBuild;
        return this;
    }

    public PivotSectionParserBuilder<T> startName(String startName) {
        this.startName = startName;
        return this;
    }

    public PivotSectionParserBuilder<T> rowStartIndex(int rowStartIndex) {
        this.rowStartIndex = rowStartIndex;
        return this;
    }

    public PivotSectionParserBuilder<T> columnStartIndex(int columnStartIndex) {
        this.columnStartIndex = columnStartIndex;
        return this;
    }

    public PivotSectionParserBuilder<T> columnCount(short columnCount) {
        if(columnCount < 2) {
            throw new IllegalArgumentException("columnCount min value is 2");
        }
        this.columnCount = columnCount;
        return this;
    }

    public PivotSectionParserBuilder<T> recordClass(Class<T> recordClass) {
        this.recordClass = recordClass;
        return this;
    }

    public PivotSectionParserBuilder<T> keyValue() {
        return columnCount((short) 2);
    }

    public PivotSectionParser<T> build() {
        return new PivotSectionParser<T>(name,
                objectToBuild,
                new SectionParserContext.Builder<T>()
                        .startName(startName)
                        .rowStartIndex(rowStartIndex)
                        .columnStartIndex(columnStartIndex)
                        .columnCount(columnCount)
                        .objectClass(recordClass)
                        .descriptorMap(FieldDescriptor.loadFrom(objectToBuild == null ? null : objectToBuild.getClass()))
                        .build()
        );
    }
}