package com.chua.common.support.converter.definition;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.chua.common.support.json.Json;
import com.chua.common.support.lang.any.Any;
import com.chua.common.support.utils.StringUtils;

/**
 * 任意
 * @author CH
 */
public class AnyTypeConverter implements TypeConverter<Any> {
    @Override
    public Class<Any> getType() {
        return Any.class;
    }

    @Override
    public Any convert(Object value) {
        if(value instanceof String) {
            String string = StringUtils.trimStart(value.toString());
            if(string.startsWith("{")) {
                try {
                    return new Any(Json.toMapStringObject(string));
                } catch (Exception e) {
                    return new Any(JSON.parseObject(string, JSONReader.Feature.AllowUnQuotedFieldNames));
                }
            }

            if(string.startsWith("[")) {
                return new Any(Json.toList(string));
            }
        }
        return new Any(value);
    }
}
