package com.pcistudio.poi.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pcistudio.poi.util.GsonUtil.toJson;

public class SheetCursor {
    private static final Logger LOG = LoggerFactory.getLogger(SheetCursor.class);
    private String sectionName;
    private int sectionStartRow = 0;
    private int sectionStartColumn = 0;
    private int nextRow = 0;
    private int nextCol = 0;

    private int objectListSize;

    private int maxColByObjectListSize;

    private int maxRowByObjectListSize;

    private SectionLocation sectionLocation;

    /**
     * This is the max number of rows in a group of sections next to each other.
     */
    private int maxRowsSectionGroup;

    public SheetCursor() {
    }

    public int nextRow() {
        return nextRow;
    }

    //TODO Performance This value can be set since the beginning of the section
    public int maxRowByFieldCount() {
        return sectionStartRow + sectionLocation.getDescriptorMapSize();
    }

    //TODO Performance This value can be set since the beginning of the section
    public int maxColByObjectListSize() {
        return maxColByObjectListSize;
    }


    public int nextCol() {
        return nextCol;
    }

    private void checkOverride() {
        checkRowOverride();
        checkColumnOverride();
    }

    //TODO test both override
    private void checkColumnOverride() {
        if (!sectionLocation.isDisplayNextRow() && nextCol > sectionLocation.getColumnStartIndex()) {
            throw new IllegalStateException(String.format("About to override column %d with section=%s. " +
                            "Check that previous section is not bigger than expected. lastWrittenColumn=%d, currentSectionStartColumn=%d", sectionLocation.getColumnStartIndex()
                    , sectionName, nextCol, sectionLocation.getColumnStartIndex()));
        }
    }

    private void checkRowOverride() {
        //TODO to test this the second pivot needs to be isStartIndexSet = true
        if (sectionLocation.isDisplayNextRow() && sectionLocation.isStartIndexSet() && nextRow > sectionLocation.getRowStartIndex()) {
            throw new IllegalStateException(String.format("About to override row %d with section=%s. " +
                            "Check that previous section is not bigger than expected. lastWrittenColumn=%d, currentSectionStartColumn=%d" +
                            "For dynamic size better use startName property", sectionLocation.getRowStartIndex()
                    , sectionName, nextRow, sectionLocation.getRowStartIndex()));
        }
    }
//
//    public int nextRowStartIndex() {
//       return nextRow = sectionDescriptor.isStartIndexNotSet() ? nextRow : sectionDescriptor.getRowStartIndex();
//    }

    public void increaseRowIndex() {
        increaseRowIndex(1);
    }

    public void increaseRowIndex(int count) {
        nextRow += count;
    }

    public void increaseColIndex() {
        increaseColIndex(1);
    }

    public void increaseColIndex(int count) {
        nextCol += count;
    }

    public void endColumn() {
        increaseColIndex();
        if (nextCol <= maxColByObjectListSize) {
            nextRow = sectionStartRow;
            trace(LOG, "End column");
        } else {
            trace(LOG, "End column when section finished");
        }
    }

    public void endRow() {
        increaseRowIndex();
        if (nextRow <= maxRowByObjectListSize) {
            nextCol = sectionStartColumn;
            trace(LOG, "End row");
        } else {
            trace(LOG, "End row when section finished");
        }
    }

    public void beginSection(String sectionName, SectionLocation sectionLocation, int objectListSize) {
        this.sectionName = sectionName;
        this.sectionLocation = sectionLocation;
        this.objectListSize = objectListSize;
        checkOverride();

        if (sectionLocation.isDisplayNextRow()) {
            trace(LOG, "Begin section type=DisplayNextRow before");
            nextCol = sectionLocation.getColumnStartIndex();
            nextRow = sectionLocation.isStartIndexNotSet() ? nextRow : sectionLocation.getRowStartIndex();
            sectionStartRow = nextRow;
            sectionStartColumn = nextCol;
            trace(LOG, "Begin section type=DisplayNextRow");
        } else {
            nextCol = sectionLocation.getColumnStartIndex();
            sectionStartColumn = nextCol;
            nextRow = sectionStartRow;
        }
        this.maxColByObjectListSize = sectionLocation.getColumnStartIndex() + objectListSize;
        this.maxRowByObjectListSize = nextRow + objectListSize;

    }

    public void trace(Logger logger, String messageStart) {
//        logger.trace("{}. name={}, sectionStartRow={}, nextRow={}, nextCol={}, maxColByObjectListSize={}", messageStart, sectionName, sectionStartRow, nextRow, nextCol, maxColByObjectListSize());
        if (logger.isTraceEnabled()) {
            logger.trace("{}. {}", messageStart, toJson(this));
        }
    }

    public void debug(Logger logger, String messageStart) {
        logger.debug("{}. name={}, sectionStartRow={}, nextRow={}, nextCol={}, maxColByObjectListSize={}", messageStart, sectionName, sectionStartRow, nextRow, nextCol, maxColByObjectListSize());
    }


}
