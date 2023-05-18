package com.chua.common.support.collection;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * 多值map
 *
 * @author CH
 */
public interface MultiValueMap<K, V> {
    /**
     * 获取第一个
     *
     * @param key key
     * @return 第一个值
     */
    V getFirst(K key);

    /**
     * 添加
     * @param key k
     * @param value v
     */
    default void put(K key, V value) {
        add(key, value);
    }
    /**
     * 添加
     * @param key k
     * @param value v
     */
    default void put(K key, V[] value) {
        for (V v : value) {
            put(key, v);
        }
    }

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
    default void putAll(K key, List<V> values) {
        addAll(key, values);
    }
    /**
     * 添加数据
     *
     * @param key    they key
     * @param values the values to be added
     */
    void addAll(K key, List<V> values);

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
     * 是否包含key
     * @param key key
     * @return 是否包含key
     */
    boolean containsKey(K key);
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

    /**
     * entry
     * @return entry
     */
    Set<Map.Entry<K, List<V>>> entrySet();

    /**
     * 遍历
     * @param consumer 消费者
     */
    void forEach(BiConsumer<K, V> consumer);

    /**
     * 是否为空
     * @return 是否为空
     */
    boolean isEmpty();

    /**
     * 获取所有值
     * @return 值
     */
    List<V> values();

    /**
     * 获取所有key
     * @return key
     */
    Set<K> keySet();

    /**
     * 获取值
     * @param key key
     * @return 值
     */
    List<V> get(K key);

    /**
     * 获取值
     * @param key key
     * @return 值
     */
    V getOne(K key);
}
