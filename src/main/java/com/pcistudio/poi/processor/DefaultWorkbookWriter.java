package com.pcistudio.poi.processor;


import com.pcistudio.poi.parser.SheetParser;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.pcistudio.poi.util.PoiUtil.retrieveFieldValue;

/**
 * Write the whole object in the excel document
 */
public class DefaultWorkbookWriter extends WorkbookProcessor implements WorkbookWriter {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorkbookWriter.class);
    private Map<String, ?> sheetsData;

    public DefaultWorkbookWriter(SheetParser<?>... sheetParsers) {
        super(sheetParsers);
    }

    public DefaultWorkbookWriter(List<SheetParser<?>> list) {
        super(list);
    }

    public DefaultWorkbookWriter(boolean failIfSheetNotFound, List<SheetParser<?>> list) {
        super(failIfSheetNotFound, list);
    }

    private Sheet createSheet(Workbook workbook, String name) {
        return name == null ? workbook.createSheet() : workbook.createSheet(name);
    }

    public void write(Path path, Map<String, Object> sheetsData) throws IOException {
        if (sheetsData.isEmpty()) {
            throw new IllegalArgumentException("Sheet data cannot be empty");
        }
        checkAllSheetHasParser(sheetsData.keySet());

        try (Workbook workbook = new XSSFWorkbook();OutputStream outputStream = new FileOutputStream(path.toFile())) {

            for (SheetParser<?> sheetParser : sheetParsers) {
                if (sheetsData.containsKey(sheetParser.getSheetName())) {
                    Object sheetObj = sheetParser.sheetClass().cast(sheetsData.get(sheetParser.getSheetName()));
                    sheetParser.write(
                            createSheet(workbook, sheetParser.getSheetName()),
                            sheetObj
                            );
//Need to check SectionParserManager SectionParserManagerBuilder and
                    //The problem is that the SectionParserManager definition is statefull because if depend on the
                    // currentParser to read the correct record
                    //Now when looking at ColumnSheetParser.parser the describeSections that is the one that suppose to
                    // have the description of the sheets

                } else {
                    handleSheetNotFound(sheetParser.getSheetName());
                }
            }

            workbook.write(outputStream);
        }
    }

    @Override
    public void write(OutputStream outputStream, Object sheetsData) throws IOException {
        Objects.requireNonNull(sheetsData, "sheetsData cannot be null");
        Map<Class<?>, Field> fieldMap = loadSheetFields(sheetsData.getClass());
        checkAllSheetsHasField(fieldMap);

        try (Workbook workbook = new XSSFWorkbook()) {

            for (SheetParser<?> sheetParser : sheetParsers) {
                if (fieldMap.containsKey(sheetParser.sheetClass())) {
                    Object sheetObj = retrieveFieldValue(fieldMap.get(sheetParser.sheetClass()), sheetsData);
                    LOG.debug("Writing using parser={}", sheetParser);
                    sheetParser.write(
                            createSheet(workbook, sheetParser.getSheetName()),
                            sheetObj
                    );
                } else {
                    handleSheetNotFound(sheetParser.getSheetName());
                }
            }

            workbook.write(outputStream);
        }
    }


}