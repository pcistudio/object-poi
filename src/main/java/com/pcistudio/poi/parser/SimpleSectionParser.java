package com.pcistudio.poi.parser;

import com.pcistudio.poi.util.PoiUtil;
import com.pcistudio.poi.util.Preconditions;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

import static com.pcistudio.poi.util.PoiUtil.retrieveFieldValue;
import static com.pcistudio.poi.util.SecurityUtil.resume;
import static com.pcistudio.poi.util.SecurityUtil.sanitize;

/**
 * This class needs to be new every time that you are trying to use it because it is stateful
 * @param <T>
 */
public abstract class SimpleSectionParser<T> implements SectionParser<T> {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleSectionParser.class);

    protected final SectionDescriptor<T> sectionDescriptor;

    protected final List<T> objectToBuild;

    private int rowCount = 0;
    private boolean started = false;

    private final String name;

    public SimpleSectionParser(String name, List<T> objectToBuild, SectionDescriptor<T> sectionDescriptor) {
        this.name = name;
        this.sectionDescriptor = sectionDescriptor;
        this.objectToBuild = objectToBuild;
//
        Preconditions.allOrNothing("objectToBuild, recordClass must to be set together. Please check builder", objectToBuild, sectionDescriptor.getRecordClass());
//        TODO data will be ignored. Probably remove
        if (this.objectToBuild == null) {
            LOG.warn("Data will be ignored for section={}", name);
        }
        if (isStartIndexNotSet() && isStartValueNotSet()) {
            throw new IllegalStateException("Section startIndex and startValue are both undefined. You need to set at least one");
        }
    }

    public String getName() {
        return name;
    }

    protected int getRowCount() {
        return rowCount;
    }

    public SectionLocation getSectionLocation() {
        return sectionDescriptor;
    }

    protected boolean isStarted() {
        return started;
    }

    protected boolean isStartIndexNotSet() {
        return sectionDescriptor.isStartIndexNotSet();
    }

    private boolean isStartValueNotSet() {
        return sectionDescriptor.isStartValueNotSet();
    }

    protected boolean isStartIndexSet() {
        return sectionDescriptor.isStartIndexSet();
    }

    private boolean isStartValueSet() {
        return sectionDescriptor.isStartValueSet();
    }

    public boolean sectionStartedByIndex(int rowIndex) {
        return rowIndex >= sectionDescriptor.getRowStartIndex();
    }

    public boolean willOverrideData(int nextIndex) {
        return nextIndex > sectionDescriptor.getRowStartIndex();
    }


    public boolean sectionStartedByName(String cellValue) {
        return sectionDescriptor.getStartValue().equals(cellValue);
    }

    public boolean isActive(Row row, int rowIndex) {
        if (started) {
            return true;
        }
        return started = isActive(row.getCell(sectionDescriptor.getColumnStartIndex()), rowIndex);
    }

    private boolean isActive(Cell cell, int rowIndex) {
        if (isStartValueSet() && isStartIndexSet()) {
            return sectionStartedByIndex(rowIndex) && sectionStartedByName(PoiUtil.cellStringTrim(cell));
        } else if (isStartIndexSet()) {
            return sectionStartedByIndex(rowIndex);
        } else {
            return sectionStartedByName(PoiUtil.cellStringTrim(cell));
        }
    }

    protected List<T> get() {
        return objectToBuild;
    }

    private boolean isFirstRow() {
        return rowCount == 0;
    }

    public void accept(Row row) {
        if (objectToBuild == null) {
            LOG.debug("Data from section={} is been ignored because objectToBuild field was null", getName());
            return;
        }
        LOG.trace("Reading row={} with parser={}", row.getRowNum(), getName());
        if (isFirstRow()) {
            doFirstRow(row);
        } else {
            doAccept(row);
        }
        rowCount++;
    }

    protected void doFirstRow(Row row) {
        doAccept(row);
    }


    /**
     * This method should get a excel Row and add some values to the objectToBuild
     * @param row excel row
     */
    protected abstract void doAccept(Row row);

    protected T newInstance() {
        return PoiUtil.create(sectionDescriptor.getRecordClass());
    }

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    protected void populateObjectFromRow(Object modelObject, String columnName, Cell valueCell) throws IllegalAccessException {
        if (StringUtil.isBlank(columnName)) {
           throw new IllegalArgumentException("columnName cannot be null");
        }
        FieldDescriptor fieldDescriptor = sectionDescriptor.getDescriptorMap().get(columnName);
        if (fieldDescriptor == null) {
            return;
        }
        String value = PoiUtil.cellStringTrim(valueCell, fieldDescriptor.getFormat());
        if (fieldDescriptor.isRequired() && value == null) {
            throw new IllegalArgumentException(String.format("Column=%s cannot be null", columnName));
        }
        Field field = fieldDescriptor.getField();
        field.setAccessible(true);
        field.set(modelObject, value);
        field.setAccessible(false);
    }

    public void notifyCompletion() {
        printResume();
        LOG.debug("Section '{}' completed", getName());
    }

    protected int getSectionLastCellIndex(Row row) {
        return sectionDescriptor.getColumnCount() != null
                ? sectionDescriptor.getColumnStartIndex() + sectionDescriptor.getColumnCount()
                : row.getLastCellNum();

    }

    protected void printResume() {

    }

    @Override
    @SuppressWarnings("PMD.EmptyFinalizer")
    protected final void finalize() {
    }

    /**
     * Write to the sheet and return the count of records written
      * @param sheet
     * @param nextIndex last row written, useful to check that we are not overriding written
     * @return count of records written
     */
    //TODO check if nextIndex can come from the sheet
    public abstract void write(Sheet sheet, SheetCursor cursor);

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), getName());
    }

    //TODO make test for all the combinations pivot-table in the same row probably with 3 sections
    protected Row getOrCreateRow(Sheet sheet, SheetCursor cursor) {
        if (!sectionDescriptor.isDisplayNextRow()) {
            return Optional
                    .ofNullable(sheet.getRow(cursor.nextRow()))
                    .orElse(sheet.createRow(cursor.nextRow()));
        }
        return sheet.createRow(cursor.nextRow());
    }

    public int objectToBuildSize() {
        return objectToBuild == null ? 0 : objectToBuild.size();
    }

    protected void logCellValue(Logger logger, Sheet sheet, SheetCursor cursor, FieldDescriptor fieldDescriptor, T obj) {
        if (logger.isTraceEnabled()) {
            logger.trace("Adding value={} into row={}, col={} for sheet={}, section={}",
                    resume(retrieveFieldValue(fieldDescriptor, obj).toString()), cursor.nextRow(), cursor.nextCol(), sanitize(sheet.getSheetName()), sanitize(getName()));
        }

    }

    protected void logColumnName(Logger logger,Sheet sheet, SheetCursor cursor, FieldDescriptor fieldDescriptor) {
        if (logger.isTraceEnabled()) {
            logger.trace("Setting columnName={} in  row={}, col={} for sheet={}, section={}",
                    sanitize(fieldDescriptor.getName()), cursor.nextRow(), cursor.nextCol(), sanitize(sheet.getSheetName()), sanitize(getName()));
        }
    }

}