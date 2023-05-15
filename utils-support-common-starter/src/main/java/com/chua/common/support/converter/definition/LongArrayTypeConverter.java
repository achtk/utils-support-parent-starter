package com.chua.common.support.converter.definition;

import com.chua.common.support.json.Json;
import com.chua.common.support.json.TypeReference;

import java.util.List;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * Long数组转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/5
 */
public class LongArrayTypeConverter implements TypeConverter<Long[]> {

    @Override
    public Long[] convert(Object value) {
        if (null == value) {
            return new Long[0];
        }

        Long[] valueArray = transToArray(value, Long.class);
        if (valueArray.length != 0) {
            return valueArray;
        }

        if (value instanceof String) {
            String stringValue = value.toString();
            if (stringValue.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && stringValue.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                try {
                    return transToArray(Json.fromJson(stringValue, new TypeReference<List<Long>>() {
                    }), Long.class);
                } catch (Exception ignored) {
                }
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            }
            return transToArray(stringValue.split(SYMBOL_COMMA), Long.class);
        }

        return new Long[0];
    }

    @Override
    public Class<Long[]> getType() {
        return Long[].class;
    }

}
