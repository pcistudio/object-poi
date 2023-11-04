package com.pcistudio.poi.parser;

import com.google.gson.Gson;
import com.pcistudio.poi.util.PoiUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.StreamSupport;

public class TableSectionParser<ROW_MODEL> extends SectionParser<ROW_MODEL> {
    private static final Logger LOG = LoggerFactory.getLogger(TableSectionParser.class);
    private String[] columns;

    protected TableSectionParser(String name, List<ROW_MODEL> objectToBuild, SectionDescriptor<ROW_MODEL> context) {
        super(name, objectToBuild, context);
    }

    @Override
    public void doFirstRow(Row row) {
        columns = loadColumnsName(row);
        LOG.info("Found {} columns={}", columns.length, new Gson().toJson(columns));
    }

    private String[] loadColumnsName(Row row) {
        return StreamSupport.stream(row.spliterator(), false)
                .map(PoiUtil::cellStringTrim)
                .toArray(String[]::new);
    }

    @Override
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void doAccept(Row row) {
        int columnIndex = 0;
        ROW_MODEL modelObject = newInstance();
        //TODO Remove this if
        if (row.getCell(sectionDescriptor.getColumnStartIndex()) == null) {
            return;
        }
        int sectionLastIndex = getSectionLastCellIndex(row);
        for (int i = sectionDescriptor.getColumnStartIndex(); i < sectionLastIndex; i++) {
            final Cell cell = row.getCell(i);
            if (columns[columnIndex] != null) {
                populateRowObject(modelObject, columnIndex, cell);
            } else {
                LOG.warn("Column in index {} doesn't have a name", columnIndex);
            }
            columnIndex++;
        }

        //noinspection unchecked
        objectToBuild.add(modelObject);
    }

    private void populateRowObject(Object rowObject, int columnIndex, Cell valueCell) {
        try {
            populateObjectFromRow(rowObject, columns[columnIndex], valueCell);
        } catch (Exception exception) {
            LOG.error("Error populating column {} in row {}", columnIndex, getRowCount(), exception);
        }
    }

    @Override
    protected void printResume() {
        LOG.info("sectionParser='{}' found {} columns, {} rows", getName(), columns.length, get().size());
        get().stream().limit(10)
                .forEach(rowModel -> LOG.debug("{}", new Gson().toJson(rowModel)));
    }

    @Override
    public void write(Sheet sheet, SheetCursor cursor) {

        //TODO: in this line "nextIndex + 1" the 1 could be a configuration with the space between sections
        // in this example there is no space (nextIndex + spaceBetweenSection)
        // Create a context class to manage this numbers(SheetCursor) and the actual context should name a sectionDescriptor
        // Same for Pivot
        int startRowIndex = cursor.nextRowStartIndex();

        writeColumns(sheet, startRowIndex);
        cursor.increaseRowIndex();
        for (int i = 0; i < objectToBuild.size(); i++) {
            ROW_MODEL obj = objectToBuild.get(i);
            writeRow(sheet, cursor.nextRow(),  obj);
            cursor.increaseRowIndex();
        }
        cursor.increaseColIndex(sectionDescriptor.getMap().size());
    }

    private void writeColumns(Sheet sheet, int rowStartIndex) {
        Row row = sheet.createRow(rowStartIndex);
        int cellIndex = sectionDescriptor.getColumnStartIndex();
        for(FieldDescriptor fieldDescriptor: sectionDescriptor.getMap().values()) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellValue(fieldDescriptor.getName());
        }
    }

    private void writeRow(Sheet sheet, int rowStartIndex, ROW_MODEL obj) {
        Row row = sheet.createRow(rowStartIndex);
        int cellIndex = sectionDescriptor.getColumnStartIndex();
        for (FieldDescriptor fieldDescriptor: sectionDescriptor.getMap().values()) {
            Cell cell = row.createCell(cellIndex++);
            PoiUtil.fillCell(cell, fieldDescriptor, obj);
        }
    }
}