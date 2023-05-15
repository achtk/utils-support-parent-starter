package com.chua.common.support.converter.definition;


import com.chua.common.support.json.Json;

import java.util.List;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * Short数组转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/5
 */
public class ShortArrayTypeConverter implements TypeConverter<Short[]> {


    @Override
    public Short[] convert(Object value) {
        if (null == value) {
            return new Short[0];
        }

        Short[] valueArray = transToArray(value, Short.class);
        if (valueArray.length != 0) {
            return valueArray;
        }

        if (value instanceof String) {
            String stringValue = value.toString();
            if (stringValue.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && stringValue.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                try {
                    return transToArray(Json.fromJson(stringValue, List.class), Short.class);
                } catch (Exception ignored) {
                }
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            }
            return transToArray(stringValue.split(SYMBOL_COMMA), Short.class);
        }

        return new Short[0];
    }

    @Override
    public Class<Short[]> getType() {
        return Short[].class;
    }

}
