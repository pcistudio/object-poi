package com.pcistudio.poi.parser;

import com.google.gson.Gson;
import com.pcistudio.poi.util.PoiUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.StreamSupport;

public class TableSectionParser<ROW> extends SectionParser<ROW> {
    private static final Logger LOG = LoggerFactory.getLogger(TableSectionParser.class);
    private String[] columns;

    protected TableSectionParser(String name, List<ROW> objectToBuild, SectionParserContext<ROW> context) {
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
        Object modelObject = newInstance();
        //TODO Remove this if
        if (row.getCell(context.getColumnStartIndex()) == null) {
            return;
        }
        int sectionLastIndex = getSectionLastCellIndex(row);
        for (int i = context.getColumnStartIndex(); i < sectionLastIndex; i++) {
            final Cell cell = row.getCell(i);
            if (columns[columnIndex] != null) {
                populateRowObject(modelObject, columnIndex, cell);
            } else {
                LOG.warn("Column in index {} doesn't have a name", columnIndex);
            }
            columnIndex++;
        }

        //noinspection unchecked
        objectToBuild.add((ROW) modelObject);
    }

    private void populateRowObject(Object rowObject, int columnIndex, Cell valueCell) {
        try {
            populateRowObject(rowObject, columns[columnIndex], valueCell);
        } catch (Exception exception) {
            LOG.error("Error populating column {} in row {}", columnIndex, getRowCount(), exception);
        }
    }

    @Override
    protected void printResume() {
        LOG.info("sectionParser='{}' found {} columns, {} rows", getName(), columns.length, get().size());
        get().stream().limit(10)
                .forEach(row -> LOG.debug("{}", new Gson().toJson(row)));
    }
}