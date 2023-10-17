package com.pcistudio.poi.util;

public class Util {
    public static <T> T create(Class<T> clazz) {
         try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error creating object from class " + clazz.getName() + ". Check for default constructor", e);
        }
    }
}
