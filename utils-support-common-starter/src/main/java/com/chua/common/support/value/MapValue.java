package com.chua.common.support.value;

import java.util.*;

/**
 * 值
 *
 * @author CH
 */
public class MapValue implements KeyValue {

    private DataMapping dataMapping;
    private final Map<String, Object> map;

    public MapValue(DataMapping dataMapping, Map<String, Object> map) {
        this.dataMapping = dataMapping;
        this.map = Optional.ofNullable(map).orElse(Collections.emptyMap());
    }

    @Override
    public Object getValue(String key) {
        return map.get(key);
    }

    @Override
    public Pair getMapperValue(String key) {
        if(null == dataMapping) {
            return new Pair(key);
        }
        Pair pair = dataMapping.getMapping().get(key);
        if(null != pair) {
            return pair;
        }

        return dataMapping.getPair(key);
    }

    @Override
    public Object getValue() {
        return map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        Set<String> strings = map.keySet();
        if(null == dataMapping) {
            return strings;
        }

        Set<String> value = new LinkedHashSet<>();
        for (String string : strings) {
            Pair pair = dataMapping.getPair(string);
            value.add(pair.getName());
        }
        return value;
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return map.entrySet();
    }
}
