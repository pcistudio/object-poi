package com.pcistudio.poi.parser;

import com.pcistudio.poi.util.PoiUtil;
import com.pcistudio.poi.util.Preconditions;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

public abstract class SectionParser<T> {
    private static final Logger LOG = LoggerFactory.getLogger(SectionParser.class);

    protected final SectionParserContext<T> context;

    protected final List<T> objectToBuild;

    private int rowCount = 0;
    private boolean started = false;

    private final String name;

    public SectionParser(String name, List<T> objectToBuild, SectionParserContext<T> context) {
        this.name = name;
        this.context = context;
        this.objectToBuild = objectToBuild;
//
        Preconditions.allOrNothing("objectToBuild, recordClass must to be set together. Please check builder", objectToBuild, context.getRecordClass());
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

    protected boolean isStarted() {
        return started;
    }

    private boolean isStartIndexNotSet() {
        return context.getRowStartIndex() < 0;
    }

    private boolean isStartValueNotSet() {
        return StringUtil.isBlank(context.getStartValue());
    }

    private boolean isStartIndexSet() {
        return !isStartIndexNotSet();
    }

    private boolean isStartValueSet() {
        return !isStartValueNotSet();
    }

    public boolean sectionStartedByIndex(int rowIndex) {
        return rowIndex >= context.getRowStartIndex();
    }

    public boolean sectionStartedByName(String cellValue) {
        return context.getStartValue().equals(cellValue);
    }

    public boolean isActive(Row row, int rowIndex) {
        if (started) {
            return true;
        }
        started = isActive(row.getCell(0), rowIndex);
        if (started) {
            LOG.debug("Selected sectionParser='{}' in row={}", getName(), rowIndex);
        }
        return started;
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
        return PoiUtil.create(context.getRecordClass());
    }

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    protected void populateRowObject(Object modelObject, String columnName, Cell valueCell) throws IllegalAccessException {
        if (StringUtil.isBlank(columnName)) {
           throw new IllegalArgumentException("columnName cannot be null");
        }
        FieldDescriptor fieldDescriptor = context.getMap().get(columnName);
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
        LOG.debug("Section '{}' completed", getName());
        printResume();
    }

    protected int getSectionLastCellIndex(Row row) {
        return context.getColumnCount() != null
                ? context.getColumnStartIndex() + context.getColumnCount()
                : row.getLastCellNum();

    }

    protected void printResume() {

    }

    @Override
    @SuppressWarnings("PMD.EmptyFinalizer")
    protected final void finalize() {
    }
}