package com.pcistudio.poi.parser;

import com.pcistudio.poi.util.Util;
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
        return Util.create(sheetClass());
    }

    public T parse(Sheet sheet) {
        SectionParserManagerBuilder builder = new SectionParserManagerBuilder();
        T result = createSheetObject();
        describeSections(result, builder);
        try (SectionParserManager sectionParserManager = builder.build()) {
            describeSections(result, sectionParserManager);
            int index = 0;
            for (Row row : sheet) {
                SectionParser<?> sectionParser = sectionParserManager.get(row, index);
                sectionParser.accept(row);
                index++;
            }
            return result;
        }
    }

    protected void describeSections(T result, SectionParserManager sectionParserManager) {

    }

    protected abstract void describeSections(T result, SectionParserManagerBuilder builder);

}