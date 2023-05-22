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
public class ConcurrentReferenceTable<R, C, V> implements Table<R, C, V> {

    private final Map<R, Map<C, V>> rowMap;
    private final Map<C, Map<R, V>> columnMap;
    private final Collection<V> valueArray;
    private final int initialCapacity;

    public ConcurrentReferenceTable() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ConcurrentReferenceTable(int initialCapacity) {
        this.rowMap = new HashMap<>(initialCapacity);
        this.columnMap = new HashMap<>();
        this.valueArray = new LinkedList<>();
        this.initialCapacity = initialCapacity;
    }


    @Override
    public boolean contains(R rowKey, C columnKey) {
        return rowMap.containsKey(rowKey) && columnMap.containsKey(columnKey);
    }

    @Override
    public boolean containsRow(R rowKey) {
        return rowMap.containsKey(rowKey);
    }

    @Override
    public boolean containsColumn(C columnKey) {
        return columnMap.containsKey(columnKey);
    }

    @Override
    public boolean containsValue(V value) {
        return valueArray.contains(value);
    }

    @Override
    public V get(R rowKey, C columnKey) {
        Map<C, V> cvMap = rowMap.get(rowKey);
        return null != cvMap ? cvMap.get(columnKey) : null;
    }

    @Override
    public boolean isEmpty() {
        return rowMap.isEmpty();
    }

    @Override
    public int size() {
        return rowMap.size();
    }

    @Override
    public V put(R rowKey, C columnKey, V value) {
        columnMap.computeIfAbsent(columnKey, it -> new HashMap<>(initialCapacity))
                .put(rowKey, value);
        valueArray.add(value);
        return rowMap.computeIfAbsent(rowKey, it -> new HashMap<>(initialCapacity))
                .put(columnKey, value);
    }

    @Override
    public V remove(R rowKey, C columnKey) {
        synchronized (this) {
            removeColumn(rowKey, columnKey);
            return removeRow(rowKey, columnKey);
        }
    }

    private void removeColumn(R rowKey, C columnKey) {
        Map<R, V> rvMap = columnMap.get(columnKey);
        if (null == rvMap) {
            columnMap.remove(columnKey);
            return;
        }

        V v = rvMap.get(rowKey);
        rvMap.remove(rowKey);
        valueArray.remove(v);
    }

    private V removeRow(R rowKey, C columnKey) {
        Map<C, V> cvMap = rowMap.get(rowKey);
        if (null == cvMap) {
            rowMap.remove(rowKey);
            return null;
        }
        return cvMap.remove(columnKey);
    }

    @Override
    public Map<C, V> row(R rowKey) {
        return rowMap.get(rowKey);
    }

    @Override
    public Map<R, V> column(C columnKey) {
        return columnMap.get(columnKey);
    }

    @Override
    public V computeIfAbsent(R r, C c, BiFunction<R, C, V> function) {
        V apply = function.apply(r, c);
        rowMap.computeIfAbsent(r, r1 -> new ConcurrentReferenceHashMap<>(initialCapacity)).putIfAbsent(c, apply);
        return apply;
    }

    @Override
    public Set<R> rowKeySet() {
        return new HashSet<>(rowMap.keySet());
    }

    @Override
    public Map<C, Map<R, V>> columns() {
        return columnMap;
    }

    @Override
    public Map<C, Map<C, V>> rows() {
        return new LinkedHashMap<>();
    }

    @Override
    public Collection<V> values() {
        List<V> rs = new ArrayList<>();
        for (Map<C, V> value : rowMap.values()) {
            rs.addAll(value.values());
        }
        return Collections.unmodifiableList(rs);
    }

    @Override
    public Set<C> columnKeySet() {
        Set<C> rs = new LinkedHashSet<>();
        for (Map<C, V> value : rowMap.values()) {
            rs.addAll(value.keySet());
        }
        return rs;
    }

    @Override
    public String toString() {
        return rowMap.toString();
    }
}
