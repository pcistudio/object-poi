package com.pcistudio.poi.parser;

import java.util.List;

public class PivotSectionParserBuilder<T> {

    private static final int MIN_PIVOT_COLUMN = 2;
    private String name;
    private List<T> objectToBuild;

    private int rowStartIndex = -1;

    private int columnStartIndex = 0;

    private Short columnCount;

    private String startName;

    private Class<T> recordClass;

    public PivotSectionParserBuilder<T> withName(String name) {
        this.name = name;
        return this;
    }

    public PivotSectionParserBuilder<T> withObjectToBuild(List<T> objectToBuild) {
        this.objectToBuild = objectToBuild;
        return this;
    }

    public PivotSectionParserBuilder<T> withStartName(String startName) {
        this.startName = startName;
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

        return new PivotSectionParser<T>(name,
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