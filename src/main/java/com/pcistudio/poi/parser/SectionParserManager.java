package com.pcistudio.poi.parser;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SectionParserManager implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(SectionParserManager.class);
    private final List<SectionParser<?>> list = new ArrayList<>();

    private SectionParser<?> currentSectionParser = null;

    public SectionParserManager register(SectionParser<?> sectionParser) {
        list.add(sectionParser);
        return this;
    }

    public SectionParserManager register(List<SectionParser<?>> sectionParserList) {
        list.addAll(sectionParserList);
        return this;
    }

    public SectionParser<?> get(Row row, int rowIndex) {
        if (list.isEmpty()) {
            throw new IllegalStateException("There is none SectionParser register ");
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            SectionParser<?> sectionParser = list.get(i);
            if (sectionParser.isActive(row, rowIndex)) {
                notifyCompletion(sectionParser);
                return currentSectionParser = sectionParser;
            }
        }
        throw new IllegalStateException("SectionParser not found");
    }


    private void notifyCompletion(SectionParser<?> sectionParser) {
        if (currentSectionParser != null && !currentSectionParser.equals(sectionParser)) {
            currentSectionParser.notifyCompletion();
        }
    }

    @Override
    public void close() {
        if (currentSectionParser != null) {
            currentSectionParser.notifyCompletion();
            checkAllParserWereUsed();
        }
    }

    private void checkAllParserWereUsed() {
        if (!list.get(list.size() - 1).equals(currentSectionParser)) {
            LOG.warn("Not all parser were used. Last parser used {}", currentSectionParser);
        }
    }

    // TODO Try to separate the description from the Section Parser that is what create the tight coupling
    //TODO Check that the columnCount for table has logic if you are planning to put sections in the same rows.
    // You can determine columnCount by the number of fields in table
    public void write(Sheet sheet) {
        //TODO complete this method
        SheetCursor cursor = new SheetCursor();
        for (SectionParser<?> sectionParser : list) {
            LOG.debug("Writing in sheet={}, section={}, nextIndex={}", sheet.getSheetName(), sectionParser, cursor.nextRow());
            sectionParser.write(sheet, cursor);
            LOG.debug("Finish writing in sheet='{}', section='{}', nextIndex={}", sheet.getSheetName(), sectionParser, cursor.nextRow());
        }
        LOG.info("Write in {} Completed", sheet.getSheetName());
    }

    private SheetCursor createCursor() {
        return new SheetCursor();
    }
}
