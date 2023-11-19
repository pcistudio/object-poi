package com.pcistudio.poi.util;

import java.util.List;

public class Preconditions {

    @SuppressWarnings("PMD")
    public static void allOrNothing(String message, Object... objects) {
        notEmpty(objects, "objects in allOrNothing cannot be null");
        int notSet = 0;
        int set = 0;
        for (Object object : objects) {
            if (object == null) {
                notSet++;
            } else {
                set++;
            }
            if (set != 0 && notSet != 0) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    public static void notEmpty(Object[] objects, String message) {
        if (objects == null || objects.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(List<?> objects, String message) {
        if (objects == null || objects.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void lessThan(int number, int maxValue, String message) {
        if (number >= maxValue) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void greaterThan(short number, int minValue, String message) {
        if (number <= minValue) {
            throw new IllegalArgumentException(message);
        }
    }
}
