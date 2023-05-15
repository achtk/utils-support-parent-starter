package com.chua.common.support.collection;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * 类型集合
 *
 * @author CH
 * @version 1.0.0
 */
@EqualsAndHashCode
@NoArgsConstructor
@SuppressWarnings("ALL")
public class TypeHashMap<T> implements TypeMap<T> {

    private Map<String, Object> source;

    private static final TypeHashMap EMPTY = new TypeHashMap(Collections.emptyMap());

    public TypeHashMap(int initialCapacity) {
        this.source = new HashMap<>(initialCapacity);
    }

    public TypeHashMap(Map<String, Object> source) {
        this.source = source;
    }

    /**
     * 创建集合
     *
     * @return 集合
     */
    public static TypeHashMap create() {
        return new TypeHashMap(new LinkedHashMap<>());
    }


    /**
     * 空集合
     *
     * @return 集合
     */
    public static TypeMap<TypeHashMap> empty() {
        return EMPTY;
    }

    public static <T> TypeMap<T> empty(Class<T> expressionValueClass) {
        return EMPTY;
    }

    @Override
    public Object getObject(String key, Object defaultValue) {
        return null != source ? Optional.ofNullable(source.get(key)).orElse(defaultValue) : defaultValue;
    }

    @Override
    public Map<String, Object> source() {
        return source;
    }
}
