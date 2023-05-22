package com.chua.common.support.collection;

import java.util.*;
import java.util.function.BiFunction;

import static com.chua.common.support.constant.NumberConstant.DEFAULT_INITIAL_CAPACITY;

/**
 * 三元
 *
 * @param <R> row
 * @param <C> column
 * @param <V> value
 * @author Administrator
 */
public class ConcurrentReferenceTable<R, C, V> implements Table<R, C, V>{

    private final Map<R, Map<C, V>> rpl;
    private final int initialCapacity;
    public ConcurrentReferenceTable() {
        this(DEFAULT_INITIAL_CAPACITY);
    }
    public ConcurrentReferenceTable(int initialCapacity) {
        this.rpl = new ConcurrentReferenceHashMap<>(initialCapacity);
        this.initialCapacity = initialCapacity;
    }


    @Override
    public boolean contains(R rowKey, C columnKey) {
        boolean containsKey = rpl.containsKey(rowKey);
        if(!containsKey) {
            return false;
        }

        return rpl.get(rowKey).containsKey(columnKey);
    }

    @Override
    public boolean containsRow(R rowKey) {
        return rpl.containsKey(rowKey);
    }

    @Override
    public boolean containsColumn(C columnKey) {
        for (Map<C, V> cvMap : rpl.values()) {
            if(cvMap.containsKey(columnKey)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        for (Map<C, V> cvMap : rpl.values()) {
            if(cvMap.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(R rowKey, C columnKey) {
        Map<C, V> cvMap = rpl.get(rowKey);
        return null != cvMap ? cvMap.get(columnKey) : null;
    }

    @Override
    public boolean isEmpty() {
        return rpl.isEmpty();
    }

    @Override
    public int size() {
        Set<R> rs = rpl.keySet();
        int size = rs.size();
        Collection<Map<C, V>> values = rpl.values();
        for (Map<C, V> value : values) {
            size *= value.size();
        }
        return size;
    }

    @Override
    public V put(R rowKey, C columnKey, V value) {
        return rpl.computeIfAbsent(rowKey, it -> new ConcurrentReferenceHashMap<>(initialCapacity)).put(columnKey, value);
    }

    @Override
    public V remove(R rowKey, C columnKey) {
        synchronized (this) {
            Map<C, V> cvMap = rpl.get(rowKey);
            if(null == cvMap) {
                return null;
            }
            return cvMap.remove(columnKey);
        }
    }

    @Override
    public Map<C, V> row(R rowKey) {
        return rpl.get(rowKey);
    }

    @Override
    public Map<R, V> column(C columnKey) {
        Map<R, V> rs = new LinkedHashMap<>();
        for (Map.Entry<R, Map<C, V>> entry : rpl.entrySet()) {
            Map<C, V> entryValue = entry.getValue();
            R key = entry.getKey();
            entryValue.forEach((k, v) -> {
                rs.put(key, v);
            });
        }
        return rs;
    }

    @Override
    public V computeIfAbsent(R r, C c, BiFunction<R, C, V> function) {
        V apply = function.apply(r, c);
        rpl.computeIfAbsent(r, r1 -> new ConcurrentReferenceHashMap<>(initialCapacity)).putIfAbsent(c, apply);
        return apply;
    }

    @Override
    public Set<R> rowKeySet() {
        return new HashSet<>(rpl.keySet());
    }

    @Override
    public Map<C, Map<C, V>> rowMap() {
        Map<C, Map<C, V>> rs = new LinkedHashMap<>();
        return rs;
    }

    @Override
    public Collection<V> values() {
        List<V> rs = new ArrayList<>();
        for (Map<C, V> value : rpl.values()) {
            rs.addAll(value.values());
        }
        return Collections.unmodifiableList(rs);
    }

    @Override
    public Set<C> columnKeySet() {
        Set<C> rs = new LinkedHashSet<>();
        for (Map<C, V> value : rpl.values()) {
            rs.addAll(value.keySet());
        }
        return rs;
    }

    @Override
    public String toString() {
        List<Object> rs = new LinkedList<>();
        for (Map.Entry<R, Map<C, V>> entry : rpl.entrySet()) {
            for (Map<C, V> value : rpl.values()) {
                for (Map.Entry<C, V> cvEntry : value.entrySet()) {
                    List<Object> item = new LinkedList<>();
                    item.add(entry.getKey());
                    item.add(cvEntry.getKey());
                    item.add(cvEntry.getValue());

                    rs.add(item);
                }
            }
        }
        return rs.toString();
    }
}
