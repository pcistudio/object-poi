package com.pcistudio.poi.util;

import com.pcistudio.poi.parser.FieldDescriptor;
import com.pcistudio.poi.report.CarRentalHeaderStrongTyped;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static com.pcistudio.poi.util.Util.ioStream;
import static org.junit.jupiter.api.Assertions.*;

class PoiUtilTest {
    @Test
    void testDump() throws IOException {
        try (InputStream inputStream = ioStream("CarRental.xlsx")) {
            PoiUtil.dumpSheet(inputStream, "Revenue");
        }
    }

    @Test
    void testFillCell() {
        CarRentalHeaderStrongTyped carRentalHeader = new CarRentalHeaderStrongTyped();
        carRentalHeader.setReportTitle("Test Write Report");
        carRentalHeader.setReportDate(LocalDateTime.now());
        carRentalHeader.setNumberOfClients(7);
        carRentalHeader.setTotalRecords(12);
        Map<String, FieldDescriptor> fieldDescriptorMap = FieldDescriptor.loadFrom(CarRentalHeaderStrongTyped.class);

        Cell reportTitleCell = createCell();
        PoiUtil.fillCell(reportTitleCell, fieldDescriptorMap.get("Report Title:"), carRentalHeader);
        assertEquals("Test Write Report", reportTitleCell.getStringCellValue());

        Cell reportDateCell = createCell();
        PoiUtil.fillCell(reportDateCell, fieldDescriptorMap.get("Report Date"), carRentalHeader);
        assertNotNull(reportDateCell.getLocalDateTimeCellValue());

        Cell numOfClientsCell = createCell();
        PoiUtil.fillCell(numOfClientsCell, fieldDescriptorMap.get("Number of Clients"), carRentalHeader);
        assertEquals(7, numOfClientsCell.getNumericCellValue());

        Cell totalRecordsCell = createCell();
        PoiUtil.fillCell(totalRecordsCell, fieldDescriptorMap.get("Total Records"), carRentalHeader);
        assertEquals(12, totalRecordsCell.getNumericCellValue());
    }

    Cell createCell() {
        return new Cell() {
            Double numValue;
            String strValue;

            LocalDateTime dateValue;

            @Override
            public int getColumnIndex() {
                return 0;
            }

            @Override
            public int getRowIndex() {
                return 0;
            }

            @Override
            public Sheet getSheet() {
                return null;
            }

            @Override
            public Row getRow() {
                return null;
            }

            @Override
            public void setCellType(CellType cellType) {

            }

            @Override
            public void setBlank() {

            }

            @Override
            public CellType getCellType() {
                return null;
            }

            @Override
            public CellType getCachedFormulaResultType() {
                return null;
            }

            @Override
            public void setCellValue(double value) {
                numValue = value;
            }

            @Override
            public void setCellValue(Date value) {

            }

            @Override
            public void setCellValue(LocalDateTime value) {
                dateValue = value;
            }

            @Override
            public void setCellValue(Calendar value) {

            }

            @Override
            public void setCellValue(RichTextString value) {

            }

            @Override
            public void setCellValue(String value) {
                strValue = value;
            }

            @Override
            public void setCellFormula(String formula) throws FormulaParseException, IllegalStateException {

            }

            @Override
            public void removeFormula() throws IllegalStateException {

            }

            @Override
            public String getCellFormula() {
                return null;
            }

            @Override
            public double getNumericCellValue() {
                return numValue;
            }

            @Override
            public Date getDateCellValue() {
                return null;
            }

            @Override
            public LocalDateTime getLocalDateTimeCellValue() {
                return dateValue;
            }

            @Override
            public RichTextString getRichStringCellValue() {
                return null;
            }

            @Override
            public String getStringCellValue() {
                return strValue;
            }

            @Override
            public void setCellValue(boolean value) {

            }

            @Override
            public void setCellErrorValue(byte value) {

            }

            @Override
            public boolean getBooleanCellValue() {
                return false;
            }

            @Override
            public byte getErrorCellValue() {
                return 0;
            }

            @Override
            public void setCellStyle(CellStyle style) {

            }

            @Override
            public CellStyle getCellStyle() {
                return null;
            }

            @Override
            public void setAsActiveCell() {

            }

            @Override
            public CellAddress getAddress() {
                return null;
            }

            @Override
            public void setCellComment(Comment comment) {

            }

            @Override
            public Comment getCellComment() {
                return null;
            }

            @Override
            public void removeCellComment() {

            }

            @Override
            public Hyperlink getHyperlink() {
                return null;
            }

            @Override
            public void setHyperlink(Hyperlink link) {

            }

            @Override
            public void removeHyperlink() {

            }

            @Override
            public CellRangeAddress getArrayFormulaRange() {
                return null;
            }

            @Override
            public boolean isPartOfArrayFormulaGroup() {
                return false;
            }
        };
    }
}