package com.pcistudio.poi.util;

public class SecurityUtil {
    public static String sanitize(String userData) {
        return userData
                .replaceAll("\n", "")
                .replaceAll("\t", "")
                .replaceAll("\r", "");
    }

    public static String resume(String userData, int maxSize) {
        if (userData == null) {
            return null;
        }
        if (maxSize >= userData.length() )
            return userData;
        else
            return userData.substring(0, maxSize - 3) + "...";
    }

    public static String resume(String userData) {
        return resume(userData, 40);
    }
}