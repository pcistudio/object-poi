package com.pcistudio.poi.parser;

import com.pcistudio.poi.util.PoiUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.StreamSupport;

import static com.pcistudio.poi.util.GsonUtil.toJson;

public class TableSectionParser<ROW_MODEL> extends SimpleSectionParser<ROW_MODEL> {
    private static final Logger LOG = LoggerFactory.getLogger(TableSectionParser.class);
    private String[] columns;

    protected TableSectionParser(String name, List<ROW_MODEL> objectToBuild, SectionDescriptor<ROW_MODEL> context) {
        super(name, objectToBuild, context);
    }

    @Override
    public void doFirstRow(Row row) {
        columns = loadColumnsName(row);
        LOG.info("Found {} columns={}", columns.length, toJson(columns));
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
                .forEach(rowModel -> LOG.debug("{}", toJson(rowModel)));
    }

    @Override
    public void write(Sheet sheet, SheetCursor cursor) {

        //TODO: in this line "nextIndex + 1" the 1 could be a configuration with the space between sections
        // in this example there is no space (nextIndex + spaceBetweenSection)
        // Create a context class to manage this numbers(SheetCursor) and the actual context should name a sectionDescriptor
        // Same for Pivot

        writeColumns(sheet, cursor);
        for (int i = 0; i < objectToBuild.size(); i++) {
            ROW_MODEL obj = objectToBuild.get(i);
            writeRow(sheet, cursor,  obj);
        }
        cursor.increaseColIndex(sectionDescriptor.getDescriptorMap().size());
    }

    private void writeColumns(Sheet sheet, SheetCursor cursor) {
        Row row = getOrCreateRow(sheet, cursor);
        for(FieldDescriptor fieldDescriptor: sectionDescriptor.getDescriptorMap().values()) {

            Cell cell = row.createCell(cursor.nextCol());
            cell.setCellValue(fieldDescriptor.getName());
            logColumnName(LOG, sheet, cursor, fieldDescriptor);
            cursor.increaseColIndex();
        }
        cursor.endRow();
    }

    private void writeRow(Sheet sheet, SheetCursor cursor, ROW_MODEL obj) {
        Row row = getOrCreateRow(sheet, cursor);
        for (FieldDescriptor fieldDescriptor: sectionDescriptor.getDescriptorMap().values()) {
            Cell cell = row.createCell(cursor.nextCol());
            PoiUtil.fillCell(cell, fieldDescriptor, obj);
            logCellValue(LOG, sheet, cursor, fieldDescriptor, obj);
            cursor.increaseColIndex();
        }
        cursor.endRow();
    }
}