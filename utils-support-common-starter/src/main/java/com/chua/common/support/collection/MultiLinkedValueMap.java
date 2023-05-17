package com.chua.common.support.collection;

import com.chua.common.support.utils.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * map
 *
 * @author CH
 */
public class MultiLinkedValueMap<K, V> implements MultiValueMap<K, V>, Serializable {

    private Map<K, List<V>> targetMap;

    public MultiLinkedValueMap(Map<K, V> targetMap) {
        this();
        targetMap.forEach(this::add);
    }

    public MultiLinkedValueMap(MultiValueMap<K, V> targetMap) {
        addAll(targetMap);
    }

    public MultiLinkedValueMap() {
        this.targetMap = new LinkedHashMap<>();
    }

    @Override
    public V getFirst(K key) {
        List<V> values = this.targetMap.get(key);
        return (values != null && !values.isEmpty() ? values.get(0) : null);
    }

    @Override
    public void add(K key, V value) {
        List<V> values = this.targetMap.computeIfAbsent(key, k -> new LinkedList<>());
        values.add(value);
    }

    @Override
    public void addAll(K key, List<V> values) {
        List<V> currentValues = this.targetMap.computeIfAbsent(key, k -> new LinkedList<>());
        currentValues.addAll(values);
    }

    @Override
    public void addAll(MultiValueMap<K, V> values) {
        for (Map.Entry<K, List<V>> entry : values.entrySet()) {
            addAll(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void set(K key, V value) {
        List<V> values = new LinkedList<>();
        values.add(value);
        this.targetMap.put(key, values);
    }

    @Override
    public void setAll(Map<K, V> values) {
        values.forEach(this::set);
    }

    @Override
    public Map<K, V> toSingleValueMap() {
        Map<K, V> singleValueMap = new LinkedHashMap<>();
        this.targetMap.forEach((key, values) -> {
            if (values != null && !values.isEmpty()) {
                singleValueMap.put(key, values.get(0));
            }
        });
        return singleValueMap;
    }

    @Override
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return targetMap.entrySet();
    }

    @Override
    public void forEach(BiConsumer<K, V> consumer) {
        for (Map.Entry<K, List<V>> entry : targetMap.entrySet()) {
            K key = entry.getKey();
            entry.getValue().forEach(v -> {
                consumer.accept(key, v);
            });
        }
    }

    @Override
    public boolean isEmpty() {
        return targetMap.isEmpty();
    }

    @Override
    public List<V> values() {
        Collection<List<V>> values = targetMap.values();
        List<V> rs = new LinkedList<>();
        for (List<V> value : values) {
            rs.addAll(value);
        }
        return rs;
    }

    @Override
    public Set<K> keySet() {
        return targetMap.keySet();
    }

    @Override
    public List<V> get(K key) {
        return  targetMap.get(key);
    }

    @Override
    public V getOne(K key) {
        return CollectionUtils.findLast(targetMap.get(key));
    }

    // Map implementation

    @Override
    public boolean containsKey(Object key) {
        return this.targetMap.containsKey(key);
    }

    @Override
    public boolean equals(Object other) {
        return (this == other || this.targetMap.equals(other));
    }

    @Override
    public int hashCode() {
        return this.targetMap.hashCode();
    }

    @Override
    public String toString() {
        return this.targetMap.toString();
    }
}
