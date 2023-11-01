package com.pcistudio.poi.parser;

import java.util.Collections;
import java.util.Map;

public class SectionParserContext<T> {
    private int rowStartIndex = -1;
    private String startValue;

    private Map<String, FieldDescriptor> map;
    private Class<T> recordClass;
    private int columnStartIndex = 0;

    private Short columnCount;

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

    public static class Builder<T> {
        private final SectionParserContext<T> context = new SectionParserContext<>();

        /**
         * descriptorMap allow null when only want to parse the section but don't want to keep the data
         * can be seen as a skip section in this case
         * same with objectClass
         *
         * @param descriptorMap
         * @return
         */
        public Builder<T> descriptorMap(Map<String, FieldDescriptor> descriptorMap) {

            context.map = Collections.unmodifiableMap(descriptorMap);
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
            context.recordClass = objectClass;
            return this;
        }

        public Builder<T> columnStartIndex(int columnStartIndex) {
            if (columnStartIndex < 0) {
                throw new IllegalArgumentException("columnStartIndex can not be less that zero");
            }
            context.columnStartIndex = columnStartIndex;
            return this;
        }

        public Builder<T> columnCount(Short columnCount) {
            context.columnCount = columnCount;
            return this;
        }

        public Builder<T> rowStartIndex(int rowStartIndex) {
            context.rowStartIndex = rowStartIndex;
            return this;
        }

        public Builder<T> startValue(String startValue) {
            context.startValue = startValue;
            return this;
        }

        public SectionParserContext<T> build() {
            return context;
        }
    }
}