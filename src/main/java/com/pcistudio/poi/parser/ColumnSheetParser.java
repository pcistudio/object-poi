package com.pcistudio.poi.parser;

import com.pcistudio.poi.util.PoiUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;

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
                    SectionParser<?> sectionParser = sectionParserManager.get(row, index);
                    sectionParser.accept(row);
                } else {
                    LOG.trace("Empty row {}", index);
                }
            }
            return result;
        }
    }

    protected void describeSections(T result, SectionParserManager sectionParserManager) {

    }

    protected abstract void describeSections(T result, SectionParserManagerBuilder builder);

}
