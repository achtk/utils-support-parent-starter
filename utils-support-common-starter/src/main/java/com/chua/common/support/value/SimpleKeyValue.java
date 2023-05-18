package com.chua.common.support.value;


import com.chua.common.support.utils.MapUtils;

import java.util.*;

/**
 * 值
 *
 * @author CH
 */
public class SimpleKeyValue implements KeyValue {

    private Object key;

    private Object value;

    public SimpleKeyValue(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Object getValue(String key) {
        return value;
    }

    @Override
    public Pair getMapperValue(String key) {
        return new Pair(key);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public int size() {
        return null != key ? 1 : 0;
    }

    @Override
    public boolean isEmpty() {
        return null != key;
    }

    @Override
    public boolean containsKey(Object key) {
        return null != this.key && this.key.equals(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return null != this.value && this.value.equals(value);
    }

    @Override
    public Object get(Object key) {
        return key;
    }

    @Override
    public Object put(String key, Object value) {
        this.key = key;
        this.value = value;
        return value;
    }

    @Override
    public Object remove(Object key) {
        if(!containsKey(key)) {
            return null;
        }
        this.key = null;
        this.value = null;
        return this.value;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        Entry<? extends String, ?> entry = MapUtils.getFirst(m);
        if(null == entry) {
            return;
        }
        put(entry.getKey(), entry.getValue());
    }

    @Override
    public void clear() {
        this.key = null;
        this.value = null;
    }

    @Override
    public Set<String> keySet() {
        return null == key ? Collections.emptySet() : Collections.singleton(key.toString());
    }

    @Override
    public Collection<Object> values() {
        return Collections.singletonList(value);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return new HashSet<>();
    }
}
