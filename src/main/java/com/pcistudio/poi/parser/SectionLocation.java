package com.pcistudio.poi.parser;


import org.apache.poi.util.StringUtil;

public interface SectionLocation {

    int getRowStartIndex();

//    String getStartValue();

//    Map<String, FieldDescriptor> getDescriptorMap();

    int getDescriptorMapSize();

//    Class<T> getRecordClass();

    Short getColumnCount();

//    boolean isKeyValue();

    int getColumnStartIndex();

    boolean isDisplayNextRow();

    default boolean isStartIndexNotSet() {
        return getRowStartIndex() < 0;
    }

    default boolean isStartIndexSet() {
        return !isStartIndexNotSet();
    }
}