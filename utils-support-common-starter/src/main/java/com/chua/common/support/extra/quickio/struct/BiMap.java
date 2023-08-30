package com.chua.common.support.extra.quickio.struct;

import com.google.common.collect.HashBiMap;

import java.util.function.BiConsumer;

/**
 * map
 * @param <K> 类型
 * @param <V> 类型
 * @author Administrator
 */
public final class BiMap<K, V> {

    private final com.google.common.collect.BiMap<K, V> biMap = HashBiMap.create();


    public BiMap<K, V> put(K key, V value) {
        biMap.put(key, value);
        return this;
    }


    public V forcePut(K key, V value) {
        return biMap.forcePut(key, value);
    }


    public V getValue(K key) {
        return biMap.get(key);
    }


    public V getValue(K key, V defaultValue) {
        return biMap.getOrDefault(key, defaultValue);
    }


    public K getKey(V value) {
        return biMap.inverse().get(value);
    }


    public K getKey(V value, K defaultKey) {
        return biMap.inverse().getOrDefault(value, defaultKey);
    }


    public void remove(K key) {
        biMap.remove(key);
    }


    public void remove(K key, V value) {
        biMap.remove(key, value);
    }


    public void forEach(BiConsumer<K, V> consumer) {
        biMap.forEach(consumer);
    }

}