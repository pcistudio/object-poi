package com.pcistudio.poi.parser;


import org.apache.poi.util.StringUtil;

import java.util.Collections;
import java.util.Map;

public class SectionDescriptor<T> implements SectionLocation {

    private int rowStartIndex = -1;
    private String startValue;

    private transient Map<String, FieldDescriptor> descriptorMap;

    private int descriptorMapSize;

    private Class<T> recordClass;
    private int columnStartIndex = 0;

    private Short columnCount;

    private Integer rowCount;

    private boolean displayNextRow = true;

    public int getRowStartIndex() {
        return rowStartIndex;
    }

    public String getStartValue() {
        return startValue;
    }

    public Map<String, FieldDescriptor> getDescriptorMap() {
        return descriptorMap;
    }

    public int getRowCount() {
        return descriptorMapSize;
    }

    public Class<T> getRecordClass() {
        return recordClass;
    }

    public Short getColumnCount() {
        return columnCount;
    }

    public boolean isKeyValue() {
        return columnCount == 2;
    }

    public int getColumnStartIndex() {
        return columnStartIndex;
    }

    public boolean isDisplayNextRow() {
        return displayNextRow;
    }

    public boolean isDisplaySameRow() {
        return !isDisplayNextRow();
    }

    public boolean isStartValueNotSet() {
        return StringUtil.isBlank(getStartValue());
    }

    public boolean isStartValueSet() {
        return !isStartValueNotSet();
    }

    public static class Builder<T> {
        private final SectionDescriptor<T> descriptor = new SectionDescriptor<>();

        /**
         * descriptorMap allow null when only want to parse the section but don't want to keep the data
         * can be seen as a skip section in this case
         * same with objectClass
         *
         * @param descriptorMap
         * @return
         */
        public Builder<T> descriptorMap(Map<String, FieldDescriptor> descriptorMap) {

            descriptor.descriptorMap = Collections.unmodifiableMap(descriptorMap);
            descriptor.descriptorMapSize = descriptor.descriptorMap.size();
            return this;
        }

        /**
         * objectClass allow null when only want to parse the section but don't want to keep the data
         * can be seen as a skip section in this case
         * same with descriptorMap
         *
         * @param objectClass
         * @return
         */
        public Builder<T> recordClass(Class<T> objectClass) {
            descriptor.recordClass = objectClass;
            return this;
        }

        public Builder<T> columnStartIndex(int columnStartIndex) {
            if (columnStartIndex < 0) {
                throw new IllegalArgumentException("columnStartIndex can not be less that zero");
            }
            descriptor.columnStartIndex = columnStartIndex;
            return this;
        }

        public Builder<T> displayNextRow(boolean displayNextRow) {
            descriptor.displayNextRow = displayNextRow;
            return this;
        }

        public Builder<T> columnCount(Short columnCount) {
            descriptor.columnCount = columnCount;
            return this;
        }

        public Builder<T> rowCount(Integer rowCount) {
            descriptor.rowCount = rowCount;
            return this;
        }

        public Builder<T> rowStartIndex(int rowStartIndex) {
            descriptor.rowStartIndex = rowStartIndex;
            return this;
        }

        public Builder<T> startValue(String startValue) {
            descriptor.startValue = startValue;
            return this;
        }

        public SectionDescriptor<T> build() {
            return descriptor;
        }
    }
}