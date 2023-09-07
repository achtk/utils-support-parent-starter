package com.chua.common.support.collection;

import com.google.common.collect.HashBasedTable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author CH
 */
public class GuavaHashBasedTable<R, C, V> implements  Table<R, C, V>{

    final com.google.common.collect.Table<R, C, V> table = HashBasedTable.create();

    @Override
    public Map<R, V> column(C columnKey) {
        return table.column(columnKey);
    }

    @Override
    public Set<Cell<R, C, V>> cellSet() {
        return table.cellSet();
    }

    @Override
    public Map<C, V> row(R rowKey) {
        return table.row(rowKey);
    }

    @Override
    public boolean contains(Object rowKey, Object columnKey) {
        return table.contains(rowKey, columnKey);
    }

    @Override
    public boolean containsRow(Object rowKey) {
        return table.containsRow(rowKey);
    }

    @Override
    public boolean containsColumn(Object columnKey) {
        return table.containsColumn(columnKey);
    }

    @Override
    public boolean containsValue(Object value) {
        return table.containsValue(value);
    }

    @Override
    public V get(Object rowKey, Object columnKey) {
        return table.get(rowKey, columnKey);
    }

    @Override
    public boolean isEmpty() {
        return table.isEmpty();
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public void clear() {
        table.clear();
    }

    @Override
    public V put(R rowKey, C columnKey, V value) {
        return table.put(rowKey, columnKey, value);
    }

    @Override
    public void putAll(com.google.common.collect.Table<? extends R, ? extends C, ? extends V> table) {
        Map<? extends R, ? extends Map<? extends C, ? extends V>> mapMap = table.rowMap();
        for (Map.Entry<? extends R, ? extends Map<? extends C, ? extends V>> entry : mapMap.entrySet()) {
            Map<? extends C, ? extends V> value = entry.getValue();
            for (Map.Entry<? extends C, ? extends V> entry1 : value.entrySet()) {
                put(entry.getKey(), entry1.getKey(), entry1.getValue());
            }
        }
    }

    @Override
    public V remove(Object rowKey, Object columnKey) {
        return table.remove(rowKey, columnKey);
    }

    @Override
    public V computeIfAbsent(R r, C c, BiFunction<R, C, V> function) {
        return table.put(r, c, function.apply(r, c));
    }

    @Override
    public Set<R> rowKeySet() {
        return table.rowKeySet();
    }

    @Override
    public Map<C, Map<R, V>> columns() {
        return table.columnMap();
    }

    @Override
    public Map<R, Map<C, V>> rows() {
        return table.rowMap();
    }

    @Override
    public Collection<V> values() {
        return table.values();
    }

    @Override
    public Map<R, Map<C, V>> rowMap() {
        return table.rowMap();
    }

    @Override
    public Map<C, Map<R, V>> columnMap() {
        return table.columnMap();
    }

    @Override
    public Set<C> columnKeySet() {
        return table.columnKeySet();
    }
}
