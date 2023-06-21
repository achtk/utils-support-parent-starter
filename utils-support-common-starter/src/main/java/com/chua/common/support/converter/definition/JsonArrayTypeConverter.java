package com.chua.common.support.converter.definition;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.json.JsonObject;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.CollectionUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author CH
 */
public class JsonArrayTypeConverter implements TypeConverter<JsonArray> {
    @Override
    public Class<JsonArray> getType() {
        return JsonArray.class;
    }

    @Override
    public JsonArray convert(Object value) {
        if (value instanceof String) {
            try {
                return new JsonArray(JSON.parseArray((String) value));
            } catch (Exception e) {
                return new JsonArray(JSON.parseArray((String) value, JSONReader.Feature.AllowUnQuotedFieldNames));
            }
        }

        if (value instanceof byte[]) {
            return new JsonArray(JSON.parseArray((byte[]) value));
        }

        if (value instanceof Collection) {
            return new JsonArray((Collection) value);
        }

        if (value instanceof Map) {
            return new JsonArray(Collections.singletonList(value));
        }

        if (value.getClass().isArray() && Array.getLength(value) > 0) {
            return new JsonArray(ArrayUtils.toList(value));
        }

        return null;
    }
}
