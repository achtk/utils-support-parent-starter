package com.chua.common.support.json;

import com.alibaba.fastjson2.JSONPath;
import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.constant.ValueMode;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.profile.ProfileReliable;

import java.util.Map;

/**
 * json array
 *
 * @author CH
 */
public class JsonPath implements ProfileReliable {

    final JSONPath path;
    static final Map<String, JsonPath> CACHE = new ConcurrentReferenceHashMap<>(512);

    private JsonPath(String path) {
        this.path = JSONPath.of(path);
    }

    /**
     * 属于
     *
     * @param path 路径
     * @return {@link JsonPath}
     */
    public static JsonPath of(String path) {
        return CACHE.computeIfAbsent(path, it -> new JsonPath(path));
    }

    /**
     * 阅读
     *
     * @param json     json
     * @param jsonPath json路径
     * @return {@link Object}
     */
    public static Object read(String json, String jsonPath) {
        return JSONPath.extract(json, jsonPath);
    }


    /**
     * get
     * @param json   json
     * @param target 目标
     * @return {@link T}
     */
    public <T>T get(String json, Class<T> target) {
        return Converter.convertIfNecessary(this.path.extract(json), target);
    }

    /**
     * get
     * @param json   json
     * @return rs
     */
    public Object get(String json) {
        return get(json, Object.class);
    }

    @Override
    public Object getObject(String name, ValueMode valueMode) {
        return get(name);
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
        return null;
    }
}
