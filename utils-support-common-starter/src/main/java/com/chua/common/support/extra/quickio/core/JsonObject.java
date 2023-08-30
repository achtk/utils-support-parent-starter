package com.chua.common.support.extra.quickio.core;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
/**
 * 配置
 * @author CH
 */
final class JsonObject {

    private final Map<String, String> map = new LinkedHashMap<>();


    <T> JsonObject(T t) {
        beanToMap(t);
    }


    private <T> void beanToMap(T t) {
        try {
            map.put("\"_id\"", null);
            Class<?> clazz = t.getClass();
            while (clazz != null) {
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    putMap(field.getName(), field.get(t));
                }
                clazz = clazz.getSuperclass();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private void putMap(String fieldName, Object fieldValue) {
        if (fieldName == null || fieldValue == null || fieldValue instanceof Enum<?>) {
            return;
        } else {
            fieldName = "\"" + fieldName + "\"";
        }

        String z = "\u0000";
        if (fieldValue instanceof String || fieldValue instanceof Character) {
            if (!z.equals(String.valueOf(fieldValue))) {
                map.put(fieldName, "\"" + fieldValue + "\"");
            }
            return;
        }
        if (fieldValue instanceof Byte || fieldValue instanceof Short
                || fieldValue instanceof Integer || fieldValue instanceof Long
                || fieldValue instanceof Boolean || fieldValue instanceof Float
                || fieldValue instanceof Double || fieldValue instanceof BigInteger
                || fieldValue instanceof BigDecimal) {
            map.put(fieldName, String.valueOf(fieldValue));
            return;
        }
        if (fieldValue.getClass().isArray()) {
            map.put(fieldName, arrayToJsonString(fieldValue));
            return;
        }
        if (fieldValue instanceof Collection<?>) {
            map.put(fieldName, collectionToJsonString((Collection<?>) fieldValue));
            return;
        }
        if (fieldValue instanceof Map<?, ?>) {
            map.put(fieldName, mapToJsonString((Map<?, ?>) fieldValue));
            return;
        }
        map.put(fieldName, new JsonObject(fieldValue).toString());
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("{");
        map.forEach((k, v) -> Optional.ofNullable(v).ifPresent(s -> builder.append(k).append(":").append(v).append(",")));
        return builder.deleteCharAt(builder.length() - 1).append("}").toString();
    }


    private static String charArrayToJsonString(char[] chars) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (char c : chars) {
            builder.append("\"").append(c).append("\"").append(",");
        }
        return builder.deleteCharAt(builder.length() - 1).append("]").toString();
    }


    private static <T> String objectArrayToJsonString(T[] arrays) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Object o : arrays) {
            ifTextType(o, () -> {
                builder.append("\"").append(o).append("\"").append(",");
            }, () -> {
                builder.append(new JsonObject(o)).append(",");
            });
        }
        return builder.deleteCharAt(builder.length() - 1).append("]").toString();
    }


    private static String arrayToJsonString(Object object) {
        if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object).replaceAll(" ", "");
        }
        if (object instanceof short[]) {
            return Arrays.toString((short[]) object).replaceAll(" ", "");
        }
        if (object instanceof int[]) {
            return Arrays.toString((int[]) object).replaceAll(" ", "");
        }
        if (object instanceof long[]) {
            return Arrays.toString((long[]) object).replaceAll(" ", "");
        }
        if (object instanceof float[]) {
            return Arrays.toString((float[]) object).replaceAll(" ", "");
        }
        if (object instanceof double[]) {
            return Arrays.toString((double[]) object).replaceAll(" ", "");
        }
        if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        }
        if (object instanceof char[]) {
            return charArrayToJsonString((char[]) object);
        }
        if (object instanceof Object[]) {
            return objectArrayToJsonString((Object[]) object);
        }
        return null;
    }


    private static String collectionToJsonString(Collection<?> collection) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        collection.forEach(o -> {
            ifBasicType(o, () -> {
                builder.append(o).append(",");
            }, () -> {
                ifTextType(o, () -> {
                    builder.append("\"").append(o).append("\"").append(",");
                }, () -> {
                    builder.append(new JsonObject(o)).append(",");
                });
            });
        });
        return builder.deleteCharAt(builder.length() - 1).append("]").toString();
    }


    private static String mapToJsonString(Map<?, ?> map) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        map.forEach((k, v) -> Optional.ofNullable((k != null && v != null) ? k : null).ifPresent(o -> {
            builder.append("\"").append(k).append("\":");
            ifBasicType(v, () -> {
                builder.append(v).append(",");
            }, () -> {
                ifTextType(v, () -> {
                    builder.append("\"").append(v).append("\"").append(",");
                }, () -> {
                    builder.append(new JsonObject(v)).append(",");
                });
            });
        }));
        return builder.deleteCharAt(builder.length() - 1).append("}").toString();
    }


    private static void ifBasicType(Object o, Runnable runnable1, Runnable runnable2) {
        if (o instanceof Byte || o instanceof Short || o instanceof Integer
                || o instanceof Long || o instanceof Boolean || o instanceof Float
                || o instanceof Double || o instanceof BigInteger || o instanceof BigDecimal) {
            Optional.ofNullable(runnable1).ifPresent(Runnable::run);
        } else {
            Optional.ofNullable(runnable2).ifPresent(Runnable::run);
        }
    }


    private static void ifTextType(Object o, Runnable runnable1, Runnable runnable2) {
        if (o instanceof String || o instanceof Character) {
            Optional.ofNullable(runnable1).ifPresent(Runnable::run);
        } else {
            Optional.ofNullable(runnable2).ifPresent(Runnable::run);
        }
    }

}