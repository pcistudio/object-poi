package com.pcistudio.poi.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO Found 7 columns. [FY6786, 2022, FORD, F-250, 100, 24, E9*F9]. Get an exception when the none of the
// columns match the expected columns
public class FieldDescriptor {
    private static final Logger LOG = LoggerFactory.getLogger(FieldDescriptor.class);

    public static final FieldDescriptor EMPTY = new FieldDescriptor();

    private Field field;
    private String name;
    private String format;

    private boolean required;

    private FieldDescriptor() {
    }

    public FieldDescriptor(Field field, String name, String format, boolean required) {
        this.field = field;
        this.name = name;
        this.format = format;
        this.required = required;
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public boolean isRequired() {
        return required;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public static Map<String, FieldDescriptor> loadFrom(Class columnClass) {
        if (columnClass == null) {
            return new HashMap<>();
        }
        Field[] declaredFields = columnClass.getDeclaredFields();
        Map<String, FieldDescriptor> verticalFieldsMap = Stream.of(declaredFields)
                .map(field -> {
                    DataField annotation = field.getAnnotation(DataField.class);
                    return annotation == null
                            ? FieldDescriptor.EMPTY
                            : new FieldDescriptor(field, annotation.name(), annotation.format(), annotation.required());
                })
                .filter(fieldDescriptor -> FieldDescriptor.EMPTY != fieldDescriptor)
                .collect(Collectors.toMap(FieldDescriptor::getName, Function.identity()));

        LOG.debug("Loaded {} fields from {}", verticalFieldsMap.size(), columnClass);
        return verticalFieldsMap;
    }
}