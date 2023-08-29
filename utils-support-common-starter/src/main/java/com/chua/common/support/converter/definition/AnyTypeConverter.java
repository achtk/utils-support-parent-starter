package com.chua.common.support.converter.definition;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.json.Json;
import com.chua.common.support.lang.any.Any;
import com.chua.common.support.utils.StringUtils;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_BIG_PARENTHESES;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SQUARE_BRACKET;

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
            if(string.startsWith(SYMBOL_LEFT_BIG_PARENTHESES)) {
                try {
                    return new Any(Json.toMapStringObject(string));
                } catch (Exception e) {
                    return new Any(Json.getJsonObject(string));
                }
            }

            if(string.startsWith(SYMBOL_LEFT_SQUARE_BRACKET)) {
                return new Any(Json.toList(string));
            }
        }
        return new Any(value);
    }
}
