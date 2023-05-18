package com.chua.common.support.collection;


import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.utils.CollectionUtils;

import java.util.*;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_ASTERISK;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_QUESTION;


/**
 * hash 单级 Map
 *
 * @author CH
 * @version 1.0.0
 */
public class FlatHashMap implements FlatMap {

    private static final PathMatcher MATCHER = PathMatcher.INSTANCE;
    private transient final LevelsClose levelsClose = new LevelsClose();
    private transient final LevelsOpen levelsOpen = new LevelsOpen();
    private final transient Map<String, Object> flatMap;

    protected FlatHashMap() {
        this.flatMap = new HashMap<>();
    }

    protected FlatHashMap(Map<String, Object> source) {
        this.flatMap = levelsClose.apply(source);
    }

    @Override
    public void clear() {
        flatMap.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return flatMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return flatMap.containsValue(value);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return flatMap.entrySet();
    }

    @Override
    public Object get(Object key) {
        return flatMap.get(key);
    }

    @Override
    public boolean isEmpty() {
        return flatMap.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return flatMap.keySet();
    }

    @Override
    public Object put(String key, Object value) {
        return flatMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        this.flatMap.putAll(levelsClose.apply((Map<String, Object>) m));
    }

    @Override
    public void put(Object entity) {
        this.flatMap.putAll(BeanMap.create(entity));
    }

    @Override
    public List<Object> wildcard(String key) {
        Map<String, Object> values = new HashMap<>(flatMap.size());
        for (Entry<String, Object> entry : flatMap.entrySet()) {
            String entryKey = entry.getKey();
            if (isMatch(key, entryKey)) {
                values.put(entryKey, entry.getValue());
            }
        }

        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Object> apply = levelsOpen.apply(values);
        Object simpleValue = simpleValue(apply);

        if (simpleValue instanceof List) {
            return (List<Object>) simpleValue;
        }
        return Collections.singletonList(simpleValue);
    }

    @Override
    public Object remove(Object key) {
        return flatMap.remove(key);
    }

    @Override
    public int size() {
        return flatMap.size();
    }

    @Override
    public Collection<Object> values() {
        return flatMap.values();
    }

    /**
     * 初始化
     *
     * @return this
     */
    public static FlatMap create() {
        return create(Collections.emptyMap());
    }

    /**
     * 初始化
     *
     * @param source 原始数据
     * @return this
     */
    public static FlatMap create(Map<String, Object> source) {
        return new FlatHashMap(source);
    }

    /**
     * 索引匹配
     *
     * @param key      匹配数据
     * @param entryKey 索引
     * @return 匹配返回true
     */
    private boolean isMatch(String key, String entryKey) {
        if (key.contains(SYMBOL_QUESTION) || key.contains(SYMBOL_ASTERISK)) {
            return MATCHER.match(key, entryKey);
        }
        return entryKey.startsWith(key);
    }

    /**
     * 简单的集合
     *
     * @param apply 集合
     * @return 简单几个
     */
    private Object simpleValue(Map<String, Object> apply) {
        Set<String> keySet = apply.keySet();
        if (keySet.size() == 1) {
            String firstKey = CollectionUtils.find(keySet, 0);
            Object o = apply.get(firstKey);
            if (o instanceof Map) {
                apply = (Map<String, Object>) o;
                return simpleValue(apply);
            } else {
                return o;
            }
        }
        return apply;
    }
}
