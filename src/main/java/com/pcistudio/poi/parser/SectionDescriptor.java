package com.pcistudio.poi.parser;

import org.apache.poi.util.StringUtil;

import java.util.Collections;
import java.util.Map;

public class SectionDescriptor<T> {
    private int rowStartIndex = -1;
    private String startValue;

    private Map<String, FieldDescriptor> map;
    private Class<T> recordClass;
    private int columnStartIndex = 0;

    private Short columnCount;

    private boolean displayNextRow = true;

    public int getRowStartIndex() {
        return rowStartIndex;
    }

    public String getStartValue() {
        return startValue;
    }

    public Map<String, FieldDescriptor> getMap() {
        return map;
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

    public boolean isStartIndexNotSet() {
        return getRowStartIndex() < 0;
    }

    public boolean isStartValueNotSet() {
        return StringUtil.isBlank(getStartValue());
    }

    public boolean isStartIndexSet() {
        return !isStartIndexNotSet();
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

            descriptor.map = Collections.unmodifiableMap(descriptorMap);
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