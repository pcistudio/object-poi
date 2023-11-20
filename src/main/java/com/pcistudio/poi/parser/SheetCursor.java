package com.pcistudio.poi.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pcistudio.poi.util.GsonUtil.toJson;

public class SheetCursor {
    private static final Logger LOG = LoggerFactory.getLogger(SheetCursor.class);
    private String sectionName;
    private int sectionStartRow = 0;
    private int nextRow = 0;
    private int nextCol = 0;

    private SectionBox sectionBox;

    /**
     * This is the max number of rows in a group of sections next to each other.
     */
    private int maxRowsSectionGroup = 0;

    /**
     * This is the max number of rows in current section
     */
    private int maxRowIndex = 0;

    public SheetCursor() {
    }

    public int nextRow() {
        return nextRow;
    }

    public int getMaxRowIndex() {
        return maxRowIndex;
    }

    public int nextCol() {
        return nextCol;
    }

    private void checkOverride() {
        if (rowOverride() && columnOverride()) {
            throw new IllegalStateException(String.format("About to override data with section=%s. last written row=%d, col=%s . " +
                            "Section starting at row=%d, col=%d", sectionName, nextRow,  nextCol, sectionBox.getRowStartIndex(), sectionBox.getColumnStartIndex()));
        }
    }

    //TODO test both override
    private boolean columnOverride() {
        return sectionBox.isDisplaySameRow() && nextCol > sectionBox.getColumnStartIndex();
    }

    private boolean rowOverride() {
        //TODO to test this the second pivot needs to be isStartIndexSet = true
        if (sectionBox.isDisplayNextRow()) {
            return sectionBox.isStartIndexSet() && nextRow > sectionBox.getRowStartIndex();
        } else {
            return true;
        }
    }

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
        if (nextCol <= sectionBox.nextSectionColIndex()) {
            nextRow = sectionStartRow;
            trace(LOG, "End column");
        } else {
            trace(LOG, "End column when section finished");
        }
    }

    public void endRow() {
        increaseRowIndex();
        if (nextRow <= maxRowsSectionGroup) {
            nextCol = sectionBox.getColumnStartIndex();
            trace(LOG, "End row");
        } else {
            trace(LOG, "End row when section finished");
        }
    }

    public void beginSection(String sectionName, SectionBox sectionBox, int objectListSize) {
        this.sectionName = sectionName;
        this.sectionBox = sectionBox;
        checkOverride();
        maxRowIndex = sectionStartRow + sectionBox.getRowCount();

        if (sectionBox.isDisplayNextRow()) {
            trace(LOG, "Begin section type=DisplayNextRow before");
            nextCol = sectionBox.getColumnStartIndex();
            nextRow = sectionBox.isStartIndexNotSet() ? maxRowsSectionGroup : sectionBox.getRowStartIndex();
            sectionStartRow = nextRow;
            trace(LOG, "Begin section type=DisplayNextRow");
            setMaxRowsSectionGroup();
        } else {
            nextCol = sectionBox.getColumnStartIndex();
            nextRow = sectionStartRow;
            updateMaxRowsSectionGroup();
        }
    }

    void setMaxRowsSectionGroup() {
        maxRowsSectionGroup = nextSectionRowIndex();
    }

    private int nextSectionRowIndex() {
        return sectionBox.nextSectionRowIndex()
                .orElse(nextRow + sectionBox.getRowCount());
    }

    void updateMaxRowsSectionGroup() {
        maxRowsSectionGroup = Math.max(maxRowsSectionGroup, nextSectionRowIndex());
    }



    public void trace(Logger logger, String messageStart) {
        if (logger.isTraceEnabled()) {
            logger.trace("{}. {}", messageStart, toJson(this));
        }
    }

    public void debug(Logger logger, String messageStart) {
        logger.debug("{}. name={}, sectionStartRow={}, nextRow={}, nextCol={}, maxColByObjectListSize={}", messageStart, sectionName, sectionStartRow, nextRow, nextCol, sectionBox.nextSectionColIndex());
    }


}
