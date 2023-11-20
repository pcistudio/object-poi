package com.pcistudio.poi.parser;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

//TODO MAYBE Split this class in one for read and one for write with a base class
// Because the composeSectionParser wont work with the write

//TODO At the end think if it is a good idea to set the startRow from the previews section in case startRow=-1 NOT SET
public class SectionParserManager implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(SectionParserManager.class);
    private final List<SectionWriter<?>> write = new ArrayList<>();

    private final List<ReadSectionParser> read = new ArrayList<>();

    private ReadSectionParser currentSectionParser = null;

    public SectionParserManager register(SectionParser<?> sectionParser) {
        if (sectionParser.getSectionBox().isDisplayNextRow()) {
            read.add(sectionParser);
            write.add(sectionParser);
        } else {
            combineReadParser(sectionParser);
            combineWriteParser(sectionParser);
        }
        return this;
    }

    public SectionParserManager register(List<SectionParser<?>> sectionParserList) {
        sectionParserList.forEach(this::register);
        return this;
    }

    public ReadSectionParser get(Row row) {
        if (read.isEmpty()) {
            throw new IllegalStateException("There is none SectionParser register ");
        }
        for (int i = read.size() - 1; i >= 0; i--) {
            ReadSectionParser sectionParser = read.get(i);
            if (sectionParser.isActive(row)) {
                setCurrentParser(sectionParser, row.getRowNum());
                return currentSectionParser;
            }
        }
        throw new IllegalStateException("SectionParser not found");
    }

    private void combineReadParser(ReadSectionParser sectionParser) {
        if (read.isEmpty()) {
            LOG.warn("No preview reader to compose sectionParser. List is empty");
        } else {
            int last = read.size() - 1;
            ReadSectionParser composeReadSectionParser = ReadSectionParser.compose(read.get(last), sectionParser);
            LOG.trace("Created compose sectionParser={}", composeReadSectionParser);
            read.set(last, composeReadSectionParser);
        }
    }

    private void combineWriteParser(SectionWriter<?> sectionParser) {
        if (write.isEmpty()) {
            LOG.warn("No preview writer to compose sectionParser. List is empty");
        } else {
            int last = write.size() - 1;
            SectionWriter<?> composeSectionWriter = SectionWriter.compose(write.get(last), sectionParser);
            LOG.trace("Created compose sectionParser={}", composeSectionWriter);
            write.set(last, composeSectionWriter);
        }
    }

    private void setCurrentParser(ReadSectionParser sectionParser, int rowIndex) {
        if (!sectionParser.equals(currentSectionParser)) {
            if (currentSectionParser != null) {
                currentSectionParser.notifyCompletion();
            }
            currentSectionParser = sectionParser;
            LOG.debug("Selected sectionParser='{}' in row={}", sectionParser.getName(), rowIndex);
        }
    }

    @Override
    public void close() {
        if (currentSectionParser != null) {
            currentSectionParser.notifyCompletion();
            checkAllReadParserWereUsed();
        }
    }

    private void checkAllReadParserWereUsed() {
        if (!read.get(read.size() - 1).equals(currentSectionParser)) {
            LOG.warn("Not all parser were used. Last parser used {}", currentSectionParser);
            printLeftoverParsers();
        }
    }

    private void printLeftoverParsers() {
        int index = read.indexOf(currentSectionParser);
        List<ReadSectionParser> leftOverParsers = read.subList(index + 1, read.size());
        LOG.warn("Parsers never used={}", leftOverParsers);
    }

    // TODO Try to separate the description from the Section Parser that is what create the tight coupling
    //TODO Check that the columnCount for table has logic if you are planning to put sections in the same rows.
    // You can determine columnCount by the number of fields in table
    public void write(Sheet sheet) {
        //TODO complete this method
        SheetCursor cursor = new SheetCursor();

        for (SectionWriter<?> sectionParser : write) {

                sectionParser.write(sheet, cursor);
            LOG.debug("Finish writing in sheet='{}', section='{}', nextRow={}, nextCol={}", sheet.getSheetName(), sectionParser, cursor.nextRow(), cursor.nextCol());
        }
        LOG.info("Write in {} Completed", sheet.getSheetName());
    }

    public void traceWrite() {
        LOG.trace("Registered {} writer={}", write.size(), write);
    }
}
