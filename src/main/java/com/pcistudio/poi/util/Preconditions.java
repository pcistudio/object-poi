package com.pcistudio.poi.util;

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

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
