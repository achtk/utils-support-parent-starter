package com.chua.common.support.json;

import com.chua.common.support.collection.TypeMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.json.jsonpath.JsonPath;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * json object
 *
 * @author CH
 */
public class JsonObject implements TypeMap<JsonObject> {

    protected Map source = new LinkedHashMap();

    public JsonObject() {
        this(new LinkedHashMap());
    }

    public JsonObject(Map source) {
        this.source = source;
    }


    public static JsonObject create() {
        return new JsonObject();
    }

    @Override
    public Object getObject(String key, Object defaultValue) {
        return Optional.ofNullable(source.get(key)).orElse(defaultValue);
    }

    @Override
    public Map<String, Object> source() {
        return source;
    }

    @Override
    public Object get(Object key) {
        Object o = TypeMap.super.get(key);
        if (null != o) {
            return o;
        }

        if (key instanceof String) {
            return JsonPath.parse(source).read((String) key);
        }

        return null;
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    public JsonObject getJsonObject(String name) {
        return new JsonObject((Map) get(name));
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    public JsonArray getJsonArray(String name) {
        Object o = get(name);
        return new JsonArray(Converter.convertIfNecessary(o, List.class));
    }

    /**
     * 获取数据
     *
     * @param jsonObject 模板
     * @return 数据
     */
    public JsonObject getTemplate(JsonObject jsonObject) {
        JsonObject rs = new JsonObject();
        for (Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String && ((String) value).startsWith("$")) {
                rs.put(key, JsonPath.parse(source).read(value.toString()));
                continue;
            }

            if (value instanceof JsonObject) {
                rs.putAll(getTemplate((JsonObject) value));
                continue;
            }
            rs.put(key, value);
        }

        return rs;
    }

    public byte[] toJsonBytes() {
        return Json.toJsonByte(source);
    }
}
