package com.chua.common.support.converter.definition;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.chua.common.support.json.JsonObject;

import java.util.Collection;
import java.util.Map;

/**
 * @author CH
 */
public class JsonObjectTypeConverter implements TypeConverter<JsonObject> {
    @Override
    public Class<JsonObject> getType() {
        return JsonObject.class;
    }

    @Override
    public JsonObject convert(Object value) {
        if (value instanceof String) {
            try {
                return new JsonObject(JSON.parseObject((String) value));
            } catch (Exception e) {
                return new JsonObject(JSON.parseObject((String) value, JSONReader.Feature.AllowUnQuotedFieldNames));
            }
        }

        if (value instanceof byte[]) {
            return new JsonObject(JSON.parseObject((byte[]) value));
        }

        if (value instanceof Map) {
            return new JsonObject((Map) value);
        }

        return null;
    }
}
