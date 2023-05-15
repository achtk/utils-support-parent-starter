package com.chua.common.support.collection;

import java.util.List;
import java.util.Map;

/**
 * 多值map
 *
 * @author CH
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    /**
     * 获取第一个
     *
     * @param key key
     * @return 第一个值
     */
    V getFirst(K key);

    /**
     * 添加数据
     *
     * @param key   key
     * @param value 值
     */
    void add(K key, V value);

    /**
     * 添加数据
     *
     * @param key    they key
     * @param values the values to be added
     */
    void addAll(K key, List<? extends V> values);

    /**
     * 添加数据
     *
     * @param values the values to be added
     */
    void addAll(MultiValueMap<K, V> values);

    /**
     * 添加数据
     *
     * @param key   the key
     * @param value the value to be added
     * @since 5.2
     */
    default void addIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            add(key, value);
        }
    }

    /**
     * 设置数据
     *
     * @param key   key
     * @param value 值
     */
    void set(K key, V value);

    /**
     * 设置数据
     *
     * @param values 值
     */
    void setAll(Map<K, V> values);

    /**
     * 获取一个简单的map
     *
     * @return map
     */
    Map<K, V> toSingleValueMap();
}
