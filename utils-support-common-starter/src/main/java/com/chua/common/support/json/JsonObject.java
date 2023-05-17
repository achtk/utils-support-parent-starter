package com.chua.common.support.json;

import com.chua.common.support.collection.TypeHashMap;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.utils.MapUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
        this.source = MapUtils.asStringMap(source);
    }


    public static JsonObject create() {
        return new JsonObject();
    }

    public Set<String> keySet() {
        return source.keySet();
    }
}
