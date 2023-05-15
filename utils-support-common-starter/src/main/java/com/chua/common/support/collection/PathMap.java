package com.chua.common.support.collection;

import com.chua.common.support.converter.Converter;

import java.util.*;

/**
 * path map
 *
 * @author CH
 */
public interface PathMap extends Map<String, Object> {
    /**
     * 对象绑定
     *
     * @param pre    前缀
     * @param target 目标类型
     * @param <E>    类型
     */
    <E> E bind(String pre, Class<E> target);

    /**
     * 对象绑定
     *
     * @param target 目标类型
     * @param <E>    类型
     */
    default <E> E bind(Class<E> target) {
        return bind("", target);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    Object get(String key, Object defaultValue);

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default Object get(String key) {
        return get(key, null);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default String getString(String key, String defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), String.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default String getString(String key) {
        return getString(key, null);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default Boolean getBoolean(String key, Boolean defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), Boolean.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default boolean getBooleanValue(String key, boolean defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), Boolean.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default boolean getBooleanValue(String key) {
        return getBoolean(key, false);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default Byte getByte(String key, Byte defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), Byte.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default Byte getByte(String key) {
        return getByte(key, null);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default byte getByteValue(String key, byte defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), byte.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default byte getByteValue(String key) {
        return getByteValue(key, (byte) 0);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default Short getShort(String key, Short defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), Short.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default Short getShort(String key) {
        return getShort(key, null);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default short getShortValue(String key, short defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), short.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default short getShortValue(String key) {
        return getShortValue(key, (short) 0);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default Integer getInteger(String key, Integer defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), Integer.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default Integer getInteger(String key) {
        return getInteger(key, null);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default int getIntegerValue(String key, int defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), int.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default int getIntegerValue(String key) {
        return getIntegerValue(key, 0);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default Long getLong(String key, Long defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), Long.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default Long getLong(String key) {
        return getLong(key, null);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default long getLongValue(String key, long defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), long.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default long getLongValue(String key) {
        return getLongValue(key, 0L);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default Float getFloat(String key, Float defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), Float.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default Float getFloat(String key) {
        return getFloat(key, null);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default float getFloatValue(String key, float defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), float.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default float getFloatValue(String key) {
        return getFloatValue(key, 0f);
    }

    /**
     * 获取数据
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 结果
     */
    default Date getDate(String key, Date defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(get(key), Date.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default Date getDate(String key) {
        return getDate(key, null);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default PathArray getArray(String key) {
        Object o = get(key);


        if (null == o) {
            return new PathLinkedArray(new LinkedList<>());
        }

        if (o instanceof PathArray) {
            return (PathArray) o;
        }

        if (o instanceof Collection) {
            return new PathLinkedArray(new LinkedList<>((Collection<?>) o));
        }

        throw new IllegalStateException("类型不匹配");
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default PathMap getPath(String key) {
        Object o = get(key);


        if (null == o) {
            return new PathLinkedMap(new LinkedHashMap<>());
        }

        if (o instanceof PathMap) {
            return (PathMap) o;
        }

        if (o instanceof Map) {
            return new PathLinkedMap((Map) o);
        }

        throw new IllegalStateException("类型不匹配");
    }


}
