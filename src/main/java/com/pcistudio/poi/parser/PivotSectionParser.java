package com.pcistudio.poi.parser;

import com.google.gson.Gson;
import com.pcistudio.poi.util.PoiUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//TODO add pluggins for checkstyle
//TODO create the gitactions build
// Deploy the artifact in maven central
public class PivotSectionParser<T> extends SectionParser<T> {
    private static final Logger LOG = LoggerFactory.getLogger(PivotSectionParser.class);

    protected PivotSectionParser(String name, List<T> objectToBuild, SectionParserContext<T> context) {
        super(name, objectToBuild, context);
    }

    /**
     * In this case we are assuming that the value in the cell in columnStartIndex is the column name
     * and the next cell is the value so cell[columnStartIndex+1]
     * In this case the objectToBuild is being populated in the field tha has the column name=cell[columnStartIndex]
     * with the value=cell[columnStartIndex+1]
     *
     * @param row
     */
    //TODO try to change the un set to null using Integer instead of -1
    @Override
    public void doAccept(Row row) {
        try {
            String columnName = PoiUtil.cellStringTrim(row.getCell(context.getColumnStartIndex()));
            if (StringUtil.isNotBlank(columnName)) {
                int sectionLastCellIndex = getSectionLastCellIndex(row);
                int firstValue = context.getColumnStartIndex() + 1;
                for (int i = firstValue; i < sectionLastCellIndex; i++) {
                    populateObjectFromRow(objectToBuild.get(i - firstValue), columnName, row.getCell(i));
                }
            } else {
                LOG.debug("Empty line found");
            }
        } catch (Exception exception) {
            LOG.error("Error populating object for row {}", getRowCount(), exception);
            LOG.debug(new Gson().toJson(objectToBuild));
        }
    }

    @Override
    public void doFirstRow(Row row) {
        int sectionLastCellIndex = getSectionLastCellIndex(row);
        for (int i = context.getColumnStartIndex() + 1; i < sectionLastCellIndex; i++) {
            objectToBuild.add(newInstance());
        }
        doAccept(row);
    }

    @Override
    protected void printResume() {
        LOG.info("sectionParser='{}' found {} records", getName(), get().size());
        if (context.isKeyValue()) {
            LOG.debug("sectionParser='{}' result={}", getName(), new Gson().toJson(get()));
        } else {
            get().stream().limit(10)
                    .forEach(row -> LOG.debug("{}", new Gson().toJson(row)));
        }
    }

    @Override
    //FIXME until now this is design to only write by row. Can not have sections next to each other
    // to have that we need and object that keep track of the lastRowWritten and lastColumn
    // in the builder we will need sameRow() or nextRow() functions
    // and in the SectionParser we will need a some properties that tell the writer to write in the next row or in the same row.
    public int write(Sheet sheet, int nextIndex) {
        if (isStartIndexSet() && willOverrideData(nextIndex)) {
            throw new IllegalStateException(String.format("About to override row %s with sheet %s. " +
                    "Check that previous section is not bigger than expected. " +
                    "For dynamic size better use startName property", context.getRowStartIndex(), sheet.getSheetName()));
        }
        if (objectToBuild == null) {
            LOG.warn("Ignoring section={} in sheet={}", getName(), sheet.getSheetName());
            return 0;
        }
        int startRowIndex = isStartIndexNotSet() ? nextIndex : context.getRowStartIndex();

        writeColumnNames(sheet, startRowIndex);
        for (int i = 0; i < objectToBuild.size(); i++) {
            T obj = objectToBuild.get(i);
            writeColumnData(sheet, startRowIndex, context.getColumnStartIndex() + 1 + i,  obj);
        }
        LOG.info("Section={} from sheet={} completed with {} rows", getName(), sheet.getSheetName(), objectToBuild.size());
        return startRowIndex + context.getMap().size();
    }
    //TODO-0 Test that multiple sections in the same row work for read
    private void writeColumnNames(Sheet sheet, int rowStartIndex) {
        int cellIndex = context.getColumnStartIndex();
        for(FieldDescriptor fieldDescriptor: context.getMap().values()) {
            Row row = sheet.createRow(rowStartIndex++);
            LOG.debug("Created row={} in sheet={}", rowStartIndex, sheet.getSheetName());
            Cell cell = row.createCell(cellIndex);
            cell.setCellValue(fieldDescriptor.getName());
        }
    }

    private void writeColumnData(Sheet sheet, int rowStartIndex, int columnStartIndex,T obj) {
        LOG.debug("Writing in sheet={}, starting at row=[{}:{}], with column={}",
                sheet.getSheetName(), rowStartIndex, rowStartIndex + context.getMap().size(), columnStartIndex);
        for (FieldDescriptor fieldDescriptor: context.getMap().values()) {
            Row row = sheet.getRow(rowStartIndex++);
            Cell cell = row.createCell(columnStartIndex);
            PoiUtil.fillCell(cell, fieldDescriptor, obj);
        }
    }


}