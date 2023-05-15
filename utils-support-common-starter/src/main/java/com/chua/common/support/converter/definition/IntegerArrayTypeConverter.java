package com.chua.common.support.converter.definition;

import com.chua.common.support.json.Json;
import com.chua.common.support.json.TypeReference;
import com.chua.common.support.utils.ArrayUtils;

import java.util.List;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * Integer数组转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/5
 */
public class IntegerArrayTypeConverter implements TypeConverter<Integer[]> {

    @Override
    public Integer[] convert(Object value) {
        if (null == value) {
            return new Integer[0];
        }

        Integer[] valueArray = transToArray(value, Integer.class);
        if (valueArray.length != 0) {
            return valueArray;
        }

        if (value instanceof String) {
            String stringValue = value.toString();
            if (stringValue.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && stringValue.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                try {
                    return ArrayUtils.transToArray(Json.fromJson(stringValue, new TypeReference<List<Integer>>() {
                    }), Integer.class);
                } catch (Exception ignored) {
                }
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            }
            return transToArray(stringValue.split(SYMBOL_COMMA), Integer.class);
        }

        return new Integer[0];
    }

    @Override
    public Class<Integer[]> getType() {
        return Integer[].class;
    }

}
