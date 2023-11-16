package com.pcistudio.poi.parser;

import com.pcistudio.poi.util.PoiUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

public abstract class ColumnSheetParser<T> implements SheetParser<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ColumnSheetParser.class);


    public ColumnSheetParser() {

    }

    public Class<T> sheetClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private T createSheetObject() {
        return PoiUtil.create(sheetClass());
    }

    public T parse(Sheet sheet) {
        SectionParserManagerBuilder builder = new SectionParserManagerBuilder();
        T result = createSheetObject();
        describeSections(result, builder);
        try (SectionParserManager sectionParserManager = builder.build()) {
            describeSections(result, sectionParserManager);
            for (int index = 0; index <= sheet.getLastRowNum(); index++) {
                Row row = sheet.getRow(index);
                if (row != null) {
                    // compose SectionParser to be able to send the same row to both
                    // to put them together i have to relay in a property set in the builder
                    ReadSectionParser sectionParser = sectionParserManager.get(row, index);
                    sectionParser.accept(row);
                } else {
                    LOG.trace("Empty row {}", index);
                }
            }
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    public void write(Sheet sheet, Object objToWrite) {
        Objects.requireNonNull(objToWrite, "Trying to write a null object");
        if(objToWrite.getClass() != sheetClass()) {
            throw new IllegalArgumentException(String.format("Trying to write a type %s in a sheetClass=%s", objToWrite.getClass(), sheetClass()));
        }

        SectionParserManagerBuilder builder = new SectionParserManagerBuilder();
        describeSections((T)objToWrite, builder);
        try (SectionParserManager sectionParserManager = builder.build()) {
            describeSections((T)objToWrite, sectionParserManager);
            sectionParserManager.write(sheet);//here the objToWrite is not needed because the is already set
//            for (int index = 0; index <= sheet.getLastRowNum(); index++) {
//                Row row = sheet.getRow(index);
//                if (row != null) {
//                    SectionParser<?> sectionParser =
//                    sectionParser.accept(row);
//                } else {
//                    LOG.trace("Empty row {}", index);
//                }
//            }
//            return result;
        }
    }

    protected void describeSections(T result, SectionParserManager sectionParserManager) {

    }

    protected abstract void describeSections(T result, SectionParserManagerBuilder builder);

}
