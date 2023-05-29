package com.chua.common.support.collection;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 忽略大小写的Map 对KEY忽略大小写，get("Value")和get("value")获得的值相同，put进入的值也会被覆盖
 * @author CH
 */
public class CaseInsensitiveMap<V> extends HashMap<String, V> {

    public CaseInsensitiveMap(Map<? extends String, ? extends V> m) {
        super(m);
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key.toString().toLowerCase());
    }

    @Override
    public V get(Object key) {
        return super.get(key.toString().toLowerCase());
    }

    @Override
    public V put(String key, V value) {
        return super.put(key.toString().toLowerCase(), value);
    }

    @Override
    public V remove(Object key) {
        return super.remove(key.toString().toLowerCase());
    }
}
