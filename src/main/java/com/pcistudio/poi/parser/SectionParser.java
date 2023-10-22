package com.pcistudio.poi.parser;

import com.pcistudio.poi.util.PoiUtil;
import com.pcistudio.poi.util.Preconditions;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public abstract class SectionParser<T> {
    private static final Logger LOG = LoggerFactory.getLogger(SectionParser.class);

    protected final SectionParserContext context;

    protected final T objectToBuild;

    private int rowCount = 0;
    private boolean started = false;

    private String name;

    //TODO see if objectToBuild can be transform to a list<T>
    public SectionParser(String name, T objectToBuild, SectionParserContext context) {
        this.name = name;
        this.context = context;
        this.objectToBuild = objectToBuild;
//
        Preconditions.allOrNothing("objectToBuild, recordClass must to be set together. Please check builder", objectToBuild, context.getRecordClass());
//        TODO data will be ignored. Probably remove
        if (this.objectToBuild == null) {
            LOG.warn("Data will be ignored for section={}", name);
        }
        if (isStartIndexNotSet() && isStartNameNotSet()) {
            throw new IllegalStateException("Section startIndex and startName are both undefined. You need to set at least one");
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

    private boolean isStartNameNotSet() {
        return StringUtil.isBlank(context.getStartName());
    }

    private boolean isStartIndexSet() {
        return !isStartIndexNotSet();
    }

    private boolean isStartNameSet() {
        return !isStartNameNotSet();
    }

    public boolean sectionStartedByIndex(int rowIndex) {
        return rowIndex >= context.getRowStartIndex();
    }

    public boolean sectionStartedByName(String cellValue) {
        return context.getStartName().equals(cellValue);
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
        if (isStartNameSet() && isStartIndexSet()) {
            return sectionStartedByIndex(rowIndex) && sectionStartedByName(PoiUtil.cellStringTrim(cell));
        } else if (isStartIndexSet()) {
            return sectionStartedByIndex(rowIndex);
        } else {
            return sectionStartedByName(PoiUtil.cellStringTrim(cell));
        }
    }

    public T get() {
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
     * @param row
     */
    protected abstract void doAccept(Row row);

    protected Object newInstance() {
        try {
            return context.getRecordClass().getConstructor().newInstance();
        } catch (Exception ex) {
            return new RuntimeException("Not able to build object type " + context.getRecordClass() + ". Check empty constructor");
        }
    }

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    protected void populateRowObject(Object modelObject, String columnName, Cell valueCell) throws IllegalAccessException {
        if (StringUtil.isBlank(columnName)) {
           throw new IllegalArgumentException("columnName cannot be null");
        }
        FieldDescriptor fieldDescriptor = (FieldDescriptor) context.getMap().get(columnName);
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