package com.chua.common.support.extra.quickio.core;


import com.chua.common.support.extra.quickio.exception.QuException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * 对象反射
 * @param <T> 类型
 */
final class ReflectObject<T extends IoEntity> {

    private final Map<String, Field> fieldMap = new HashMap<>();
    private final T t;
    private long id;
    private long createdAt;


    ReflectObject(T t) {
        this.t = t;
        Class<?> clazz = t.getClass();
        while (clazz != null){
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if ("_id".equals(field.getName())) {
                    id = t.objectId();
                    continue;
                }
                if ("createdAt".equals(field.getName())) {
                    createdAt = t.createdAt();
                    continue;
                }
                fieldMap.put(field.getName(), field);
            }
            clazz = clazz.getSuperclass();
        }
    }


    void traverseFields(BiConsumer<String, Object> consumer) {
        try {
            for (Field field : fieldMap.values()) {
                consumer.accept(field.getName(), field.get(t));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    void traverseAnnotationFields(Class<? extends Annotation> annotationClass, BiConsumer<String, Object> consumer) {
        try {
            for (Field field : fieldMap.values()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    consumer.accept(field.getName(), field.get(t));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    boolean contains(String fieldName) {
        return fieldMap.containsKey(fieldName);
    }


    boolean containsAnnotation(Class<? extends Annotation> annotationClass) {
        for (Field field : fieldMap.values()) {
            if (field.isAnnotationPresent(annotationClass)) {
                return true;
            }
        }
        return false;
    }


    Object getValue(String fieldName) {
        try {
            Field field = fieldMap.getOrDefault(fieldName, null);
            return (field != null) ? field.get(t) : null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    void setValue(String fieldName, Object value) {
        try {
            Field field = fieldMap.getOrDefault(fieldName, null);
            if (field != null) {
                field.set(t, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    Class<?> getType(String fieldName) {
        Field field = fieldMap.getOrDefault(fieldName, null);
        return field.getType();
    }


    double getNumberValue(String fieldName) {
        switch (fieldName) {
            case "_id": return id;
            case "createdAt": return createdAt;
            default:
        }
        Object object =  getValue(fieldName);
        Optional.ofNullable(object).orElseThrow(() -> new QuException(Constants.FIELD_DOES_NOT_EXIST));
        switch (getType(fieldName).getSimpleName().toLowerCase()) {
            case "int":
            case "integer": return (Integer) getValue(fieldName);
            case "byte": return (Byte) getValue(fieldName);
            case "short": return (Short) getValue(fieldName);
            case "long": return (Long) getValue(fieldName);
            case "float": return (Float) getValue(fieldName);
            case "double": return (Double) getValue(fieldName);
            default: throw new QuException(Constants.FIELD_NOT_NUMERICAL_TYPE);
        }
    }


    T get() {
        return t;
    }

}