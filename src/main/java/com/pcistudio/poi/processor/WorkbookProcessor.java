package com.pcistudio.poi.processor;


import com.pcistudio.poi.parser.SheetParser;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkbookProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(WorkbookProcessor.class);

    protected final List<SheetParser<?>> sheetParsers;

    protected final Map<String, SheetParser<?>> parserBySheetName;

    protected final Map<Class<?>, SheetParser<?>> parserBySheetClass;

    protected boolean failIfSheetNotFound;

    public WorkbookProcessor(SheetParser<?>... sheetParsers) {
        this(List.of(sheetParsers));
    }

    public WorkbookProcessor(Collection<SheetParser<?>> list) {
        this(true, list);
    }

    public WorkbookProcessor(boolean failIfSheetNotFound, Collection<SheetParser<?>> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalStateException("SheetParser list cannot be empty");
        }
        //TODO: Try to make it a unmodified
        sheetParsers = new ArrayList<>(list);
        parserBySheetName = sheetParsers.stream()
                .collect(Collectors.toMap(SheetParser::getSheetName, object -> object));

        parserBySheetClass = sheetParsers.stream()
                .collect(Collectors.toMap(SheetParser::sheetClass, object -> object));
        this.failIfSheetNotFound = failIfSheetNotFound;
    }

    public WorkbookProcessor(boolean failIfSheetNotFound, SheetParser<?>... sheetParsers) {
        this(failIfSheetNotFound, List.of(sheetParsers));
    }

    protected void checkAllSheetsHasField(Map<Class<?>, Field> fieldMap) {
        for (SheetParser<?> sheetParser : sheetParsers) {
            if (!fieldMap.containsKey(sheetParser.sheetClass())) {
                throw new IllegalStateException(String.format("Field for class=%s not present", sheetParser.sheetClass()));
            }
        }
    }

    protected void handleSheetNotFound(String sheetName) {
        if (failIfSheetNotFound) {
            throw new IllegalStateException(String.format("Sheet=%s doesn't exist", sheetName));
        } else {
            LOG.warn("Sheet={} doesn't exist", sheetName);
        }
    }

    protected Map<Class<?>, Field> loadSheetFields(Class<?> resultClass) {
        return Arrays.stream(resultClass.getDeclaredFields())
                .collect(Collectors.toMap(Field::getType, Function.identity()));
    }

    protected void checkAllSheetHasParser(Collection<String> sheets) {
        for (String sheet: sheets) {
            if (!parserBySheetName.containsKey(sheet)) {
                handleSheetNotFound(sheet);
            }
        }
    }
}
