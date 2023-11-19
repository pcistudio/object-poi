package com.pcistudio.poi.parser;


/**
 * Interface for write Section Parser
 *
 * @param <T>
 */
//FIXME this is not a Parser name should be change
public abstract class AbstractWriteSectionParser<T> implements WriteSectionParser<T> {
        public String toString() {
            return String.format("%s[%s]:xy(%d,%d):size(%d,%d)", getClass().getSimpleName(), getName(),
                    getSectionBox().getColumnStartIndex(),getSectionBox().getRowStartIndex(), getSectionBox().getColumnCount(), getSectionBox().getRowCount());
        }

}