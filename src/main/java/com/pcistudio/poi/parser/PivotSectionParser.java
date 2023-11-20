package com.pcistudio.poi.parser;

import com.pcistudio.poi.util.PoiUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static com.pcistudio.poi.util.GsonUtil.toJson;
import static com.pcistudio.poi.util.PoiUtil.retrieveFieldValue;

//TODO add pluggins for checkstyle
//TODO create the gitactions build
// Deploy the artifact in maven central
public class PivotSectionParser<T> extends SimpleSectionParser<T> {
    private static final Logger LOG = LoggerFactory.getLogger(PivotSectionParser.class);

    protected PivotSectionParser(String name, List<T> objectToBuild, SectionDescriptor<T> context) {
        super(name, objectToBuild, context, PivotSectionBox::new);
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
            String columnName = PoiUtil.cellStringTrim(row.getCell(sectionDescriptor.getColumnStartIndex()));
            if (StringUtil.isNotBlank(columnName)) {
                int sectionLastCellIndex = getSectionLastCellIndex(row);
                int firstValue = sectionDescriptor.getColumnStartIndex() + 1;
                for (int i = firstValue; i < sectionLastCellIndex; i++) {
                    populateObjectFromRow(objectToBuild.get(i - firstValue), columnName, row.getCell(i));
                }
            } else {
                LOG.debug("Empty line found");
            }
        } catch (Exception exception) {
            LOG.error("Error populating object for row {}", getRowCount(), exception);
            LOG.debug(toJson(objectToBuild));
        }
    }


    @Override
    public void doFirstRow(Row row) {
        String columnName = PoiUtil.cellStringTrim(row.getCell(sectionDescriptor.getColumnStartIndex()));
        if (columnName == null) {
            LOG.warn("Empty cell ({},{}). ColumnName expected in section={}", sectionDescriptor.getColumnStartIndex(), row.getRowNum(), getName());
            return;
        }
        FieldDescriptor fieldDescriptor = sectionDescriptor.getDescriptorMap().get(columnName);

        if (fieldDescriptor == null) {
            LOG.warn("Column={} not present in section={}", columnName, getName());
            return;
        }

        int sectionLastCellIndex = getSectionLastCellIndex(row);
        for (int i = sectionDescriptor.getColumnStartIndex() + 1; i < sectionLastCellIndex; i++) {
            objectToBuild.add(newInstance());
        }
        doAccept(row);
    }

    @Override
    protected void printResume() {
        LOG.info("sectionParser='{}' found {} records", getName(), get().size());
        if (sectionDescriptor.isKeyValue()) {
            LOG.debug("sectionParser='{}' result={}", getName(), toJson(get()));
        } else {
            get().stream().limit(10)
                    .forEach(row -> LOG.debug("{}", toJson(row)));
        }
    }



    @Override
    //FIXME until now this is design to only write by row. Can not have sections next to each other
    // to have that we need and object that keep track of the lastRowWritten and lastColumn
    // in the builder we will need sameRow() or nextRow() functions
    // and in the SectionParser we will need a some properties that tell the writer to write in the next row or in the same row.
    public void write(Sheet sheet, SheetCursor cursor) {
        cursor.beginSection(getName(), getSectionBox(), objectToBuildSize());
        LOG.debug("Writing in sheet={}, section={}, nextRow={}, nextCol={}", sheet.getSheetName(), this, cursor.nextRow(), cursor.nextCol());
        if (objectToBuild == null) {
            LOG.warn("Ignoring section={} in sheet={}", getName(), sheet.getSheetName());
            return;
        }

        writeColumnNames(sheet, cursor);
        for (int i = 0; i < objectToBuild.size(); i++) {
            T obj = objectToBuild.get(i);
            writeColumnData(sheet, cursor,  obj);
        }
        LOG.info("Section={} from sheet={} completed with {} columns", this, sheet.getSheetName(), objectToBuild.size());
//        cursor.increaseRowIndex(sectionDescriptor.getMap().size());
    }

    //TODO-0 Test that multiple sections in the same row work for read
    private void writeColumnNames(Sheet sheet, SheetCursor cursor) {
        for(FieldDescriptor fieldDescriptor: sectionDescriptor.getDescriptorMap().values()) {
            Row row = getOrCreateRow(sheet, cursor);
            Cell cell = row.createCell(cursor.nextCol());
            cell.setCellValue(fieldDescriptor.getName());
            logColumnName(LOG, sheet, cursor, fieldDescriptor);
            cursor.increaseRowIndex();
        }
        cursor.endColumn();
    }


    private void writeColumnData(Sheet sheet, SheetCursor cursor,T obj) {
        LOG.debug("Writing in sheet={}, section={}, starting at row=[{}:{}], with column={}",
                sheet.getSheetName(), getName(), cursor.nextRow(), cursor.maxRowByFieldCount(), cursor.nextCol());
        for (FieldDescriptor fieldDescriptor: sectionDescriptor.getDescriptorMap().values()) {
            Row row = sheet.getRow(cursor.nextRow());
            Cell cell = row.createCell(cursor.nextCol());
            PoiUtil.fillCell(cell, fieldDescriptor, obj);
            logCellValue(LOG, sheet, cursor, fieldDescriptor, obj);
            cursor.increaseRowIndex();
        }
        cursor.endColumn();
    }
}