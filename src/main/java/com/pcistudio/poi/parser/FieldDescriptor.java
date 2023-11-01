package com.pcistudio.poi.parser;

import com.google.gson.internal.Primitives;
import com.pcistudio.poi.util.PoiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO Found 7 columns. [FY6786, 2022, FORD, F-250, 100, 24, E9*F9]. Get an exception when the none of the
// columns match the expected columns
public class FieldDescriptor implements Comparable<FieldDescriptor>{
    private static final Logger LOG = LoggerFactory.getLogger(FieldDescriptor.class);

    public static final FieldDescriptor EMPTY = new FieldDescriptor();

    private Field field;
    private String name;
    private String format;

    private boolean required;

    private int order;

    private FieldDescriptor() {
    }

    public Field getField() {
        return field;
    }
    public Class<?> getFieldWrapType() {
        Class<?> type = getField().getType();
        if (Primitives.isPrimitive(type)) {
            type = Primitives.wrap(type);
        }
        return type;
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

    public int getOrder() {
        return order;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public static Map<String, FieldDescriptor> loadFrom(Class<?> columnClass) {
        if (columnClass == null) {
            return new HashMap<>();
        }
        List<FieldDescriptor> fieldList = sortedFieldDescriptors(columnClass);

        Map<String, FieldDescriptor> verticalFieldsMap = fieldList.stream()
                .collect(Collectors.toMap(
                        FieldDescriptor::getName,
                        Function.identity(),
                        (fieldDescriptor, fieldDescriptor2) -> { throw new IllegalStateException("Duplicate key"); },
                        LinkedHashMap::new
                        ));

        LOG.debug("Loaded {} fields from {}", verticalFieldsMap.size(), columnClass);
        return verticalFieldsMap;
    }

    private static List<FieldDescriptor> sortedFieldDescriptors(Class<?> columnClass) {
        Field[] declaredFields = columnClass.getDeclaredFields();
        return Stream.of(declaredFields)
                .map(field -> {
                    DataField annotation = field.getAnnotation(DataField.class);
                    return annotation == null
                            ? FieldDescriptor.EMPTY
                            : FieldDescriptor.builder().withField(field)
                            .withName(annotation.name()).withFormat(annotation.format())
                            .withRequired(annotation.required()).withOrder(annotation.order())
                            .build();
                })
                .filter(fieldDescriptor -> FieldDescriptor.EMPTY != fieldDescriptor)
                .sorted()
                .collect(Collectors.toList());
    }

    public static FieldDescriptorBuilder builder() {
        return new FieldDescriptorBuilder();
    }

    @Override
    public int compareTo(FieldDescriptor fieldDescriptor) {
        return Integer.compare(this.order, fieldDescriptor.order);
    }


    public static class FieldDescriptorBuilder {
        private final FieldDescriptor fieldDescriptor = new FieldDescriptor();

        public FieldDescriptorBuilder withField(Field field) {
            fieldDescriptor.field = field;
            return this;
        }

        public FieldDescriptorBuilder withName(String name) {
            fieldDescriptor.name = name;
            return this;
        }

        public FieldDescriptorBuilder withFormat(String format) {
            fieldDescriptor.format = format;
            return this;
        }

        public FieldDescriptorBuilder withRequired(boolean required) {
            fieldDescriptor.required = required;
            return this;
        }

        public FieldDescriptorBuilder withOrder(int order) {
            fieldDescriptor.order = order;
            return this;
        }

        public FieldDescriptor build() {
            return fieldDescriptor;
        }
    }

}