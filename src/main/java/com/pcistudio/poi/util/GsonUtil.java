package com.pcistudio.poi.util;

import com.google.gson.*;

import java.lang.reflect.Type;

public class GsonUtil {
    private static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Class.class, new SimpleClassSerializer());
        GSON = builder.create();
    }

    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    public static class SimpleClassSerializer implements JsonSerializer<Class<?>> {
        @Override
        public JsonElement serialize
                (Class src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getCanonicalName());
        }
    }
}
