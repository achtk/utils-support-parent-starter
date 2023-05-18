package com.chua.common.support.json;

import com.chua.common.support.collection.TypeHashMap;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.utils.MapUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * json object
 *
 * @author CH
 */
public class JsonObject extends TypeHashMap implements Profile {

    protected Map<String, Object> source;

    public JsonObject() {
        this(new LinkedHashMap<>());
    }


    public JsonObject(Map<?, ?> source) {
        super(source);
        this.source = MapUtils.asStringObjectMap(source);
    }


    public static JsonObject create() {
        return new JsonObject();
    }

    public Set<String> keySet() {
        return source.keySet();
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return source.entrySet();
    }

    public void forEach(BiConsumer<String, Object> o) {
        source.forEach(o);
    }
}
