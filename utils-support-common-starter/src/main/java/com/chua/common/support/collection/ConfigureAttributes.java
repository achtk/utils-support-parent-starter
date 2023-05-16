package com.chua.common.support.collection;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.unit.name.NamingCase;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 集合描述
 *
 * @author CH
 */
public class ConfigureAttributes extends TypeHashMap<ConfigureAttributes> implements MapDescribe<ConfigureAttributes> {

    private final Map<String, Object> source = new LinkedHashMap<>();

    /**
     * 创建集合
     *
     * @return 集合
     */
    public static ConfigureAttributes create(Object obj) {
        if(null == obj) {
            return new ConfigureAttributes(Collections.emptyMap());
        }
        return new ConfigureAttributes(BeanMap.of(obj, true));
    }

    public ConfigureAttributes() {
        this(Collections.emptyMap());
    }

    public ConfigureAttributes(Map<?, ?> describe) {
        this.putAll(Optional.ofNullable(describe).orElse(Collections.emptyMap()));
    }

    @Override
    public Object getObject(String key, Object defaultValue) {
        Object o = source.get(key);
        if (null != o) {
            return o;
        }
        key = NamingCase.toCamelHyphen(key);
        o = source.get(key);
        if (null != o) {
            return put(key, o);
        }

        key = NamingCase.toCamelUnderscore(key);
        o = source.get(key);
        if (null != o) {
            return put(key, o);
        }
        return defaultValue;
    }

    @Override
    public Map<String, Object> source() {
        return source;
    }
}
