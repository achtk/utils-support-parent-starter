package com.chua.common.support.value;


import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.json.JsonObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 值
 *
 * @author CH
 */
public interface KeyValue extends Serializable, Map<String, Object> {
    /**
     * 获取值
     *
     * @param key 表达式
     * @return 值
     */
    Object getValue(String key);
    /**
     * 获取值
     *
     * @param key 表达式
     * @return 值
     */
    Pair getMapperValue(String key);

    /**
     * 获取值
     * @return 值
     */
    Object getValue();
    /**
     * int
     *
     * @param key          表达式
     * @param defaultValue 默认值
     * @return int
     */
    default int getIntValue(String key, int defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(getValue(key), Integer.class)).orElse(defaultValue);
    }

    /**
     * int
     *
     * @param key 表达式
     * @return int
     */
    default int getIntValue(String key) {
        return Converter.convertIfNecessary(getValue(key), int.class);
    }

    /**
     * string
     *
     * @param key          表达式
     * @param defaultValue 默认值
     * @return byte
     */
    default String getStringValue(String key, String defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(getValue(key), String.class)).orElse(defaultValue);
    }

    /**
     * string
     *
     * @param key 表达式
     * @return byte
     */
    default String getStringValue(String key) {
        return Converter.convertIfNecessary(getValue(key), String.class);
    }

    /**
     * byte
     *
     * @param defaultValue 默认值
     * @param key          表达式
     * @return byte
     */
    default byte getByteValue(String key, byte defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(getValue(key), Byte.class)).orElse(defaultValue);
    }

    /**
     * byte
     *
     * @param key 表达式
     * @return byte
     */
    default byte getByteValue(String key) {
        return Converter.convertIfNecessary(getValue(key), byte.class);
    }

    /**
     * short
     *
     * @param defaultValue 默认值
     * @param key          表达式
     * @return short
     */
    default short getShortValue(String key, short defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(getValue(key), Short.class)).orElse(defaultValue);
    }

    /**
     * short
     *
     * @param key 表达式
     * @return short
     */
    default short getShortValue(String key) {
        return Converter.convertIfNecessary(getValue(key), short.class);
    }

    /**
     * float
     *
     * @param defaultValue 默认值
     * @param key          表达式
     * @return float
     */
    default float getFloatValue(String key, float defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(getValue(key), Float.class)).orElse(defaultValue);
    }

    /**
     * float
     *
     * @param key 表达式
     * @return float
     */
    default float getFloatValue(String key) {
        return Converter.convertIfNecessary(getValue(key), float.class);
    }

    /**
     * char
     *
     * @param key 表达式
     * @return char
     */
    default char getCharValue(String key) {
        return Converter.convertIfNecessary(getValue(key), char.class);
    }

    /**
     * long
     *
     * @param defaultValue 默认值
     * @param key          表达式
     * @return long
     */
    default long getLongValue(String key, long defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(getValue(key), Long.class)).orElse(defaultValue);
    }

    /**
     * long
     *
     * @param key 表达式
     * @return long
     */
    default long getLongValue(String key) {
        return Converter.convertIfNecessary(getValue(key), long.class);
    }

    /**
     * double
     *
     * @param defaultValue 默认值
     * @param key          表达式
     * @return long
     */
    default double getDoubleValue(String key, double defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(getValue(key), Double.class)).orElse(defaultValue);
    }

    /**
     * double
     *
     * @param key 表达式
     * @return long
     */
    default double getDoubleValue(String key) {
        return Converter.convertIfNecessary(getValue(key), double.class);
    }

    /**
     * int
     *
     * @param key 表达式
     * @return int
     */
    default Integer getInteger(String key) {
        return Converter.convertIfNecessary(getValue(key), Integer.class);
    }

    /**
     * Byte
     *
     * @param key 表达式
     * @return byte
     */
    default Byte getByte(String key) {
        return Converter.convertIfNecessary(getValue(key), Byte.class);
    }

    /**
     * short
     *
     * @param key 表达式
     * @return short
     */
    default Short getShort(String key) {
        return Converter.convertIfNecessary(getValue(key), Short.class);
    }

    /**
     * float
     *
     * @param key 表达式
     * @return float
     */
    default Float getFloat(String key) {
        return Converter.convertIfNecessary(getValue(key), Float.class);
    }

    /**
     * char
     *
     * @param key 表达式
     * @return char
     */
    default Character getCharacter(String key) {
        return Converter.convertIfNecessary(getValue(key), Character.class);
    }

    /**
     * long
     *
     * @param key 表达式
     * @return long
     */
    default Long getLong(String key) {
        return Converter.convertIfNecessary(getValue(key), Long.class);
    }

    /**
     * double
     *
     * @param key 表达式
     * @return long
     */
    default Double getDouble(String key) {
        return Converter.convertIfNecessary(getValue(key), Double.class);
    }

    /**
     * Date
     *
     * @param key 表达式
     * @return Date
     */
    default Date getDate(String key) {
        return Converter.convertIfNecessary(getValue(key), Date.class);
    }

    /**
     * BigDecimal
     *
     * @param key 表达式
     * @return BigDecimal
     */
    default BigDecimal getBigDecimal(String key) {
        return Converter.convertIfNecessary(getValue(key), BigDecimal.class);
    }

    /**
     * BigInteger
     *
     * @param key 表达式
     * @return BigInteger
     */
    default BigInteger getBigInteger(String key) {
        return Converter.convertIfNecessary(getValue(key), BigInteger.class);
    }

    /**
     * JsonArray
     *
     * @param key 表达式
     * @return BigInteger
     */
    default JsonArray getJsonArray(String key) {
        Object value = getValue(key);
        if (value instanceof Collection) {
            return new JsonArray((Collection) value);
        }

        return new JsonArray(Collections.emptyList());
    }

    /**
     * JsonObject
     *
     * @param key 表达式
     * @return BigInteger
     */
    default JsonObject getJsonObject(String key) {
        Object value = getValue(key);
        if (value instanceof Map) {
            return new JsonObject((Map) value);
        }

        return new JsonObject(BeanMap.create(value));
    }

    /**
     * 获取值
     *
     * @param key    表达式
     * @param target 类型
     * @param <E>    类型
     * @return 值
     */
    default <E> E getValue(String key, Class<E> target) {
        return Converter.convertIfNecessary(getValue(key), target);
    }


}
