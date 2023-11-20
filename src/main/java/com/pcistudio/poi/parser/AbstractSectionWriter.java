package com.pcistudio.poi.parser;


/**
 * Interface for write Section Parser
 *
 * @param <T>
 */
public abstract class AbstractSectionWriter<T> implements SectionWriter<T> {
        public String toString() {
            return String.format("%s[%s]:xy(%d,%d):size(%d,%d)", getClass().getSimpleName(), getName(),
                    getSectionBox().getColumnStartIndex(),getSectionBox().getRowStartIndex(), getSectionBox().getColumnCount(), getSectionBox().getRowCount());
        }

}