package com.chua.common.support.converter.definition;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * @author CH
 */
public class JsonObjectTypeConverter implements TypeConverter<JSONObject> {
    @Override
    public Class<JSONObject> getType() {
        return JSONObject.class;
    }

    @Override
    public JSONObject convert(Object value) {
        if (value instanceof String) {
            return JSON.parseObject((String) value);
        }

        if (value instanceof byte[]) {
            return JSON.parseObject((byte[]) value);
        }
        return null;
    }
}
