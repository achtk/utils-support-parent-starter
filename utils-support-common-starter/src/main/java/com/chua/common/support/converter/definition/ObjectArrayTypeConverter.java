package com.chua.common.support.converter.definition;

import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 字符串数组转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/5
 */
public class ObjectArrayTypeConverter implements TypeConverter<Object[]> {

    final public static Pattern PATTERN = Pattern.compile("\\}[\\s]{0,},[\\s]{0,}\\{");

    @Override
    public Object[] convert(Object value) {
        if (null == value) {
            return new Object[0];
        }

        if (value instanceof String[]) {
            return (String[]) value;
        }
        if (value instanceof Integer[]) {
            return (Integer[]) value;
        }
        if (value instanceof Boolean[]) {
            return (Boolean[]) value;
        }
        if (value instanceof Short[]) {
            return (Short[]) value;
        }
        if (value instanceof Long[]) {
            return (Long[]) value;
        }
        if (value instanceof Float[]) {
            return (Float[]) value;
        }
        if (value instanceof Byte[]) {
            return (Byte[]) value;
        }
        if (value instanceof Double[]) {
            return (Double[]) value;
        }

        if (value instanceof String) {
            String stringValue = value.toString();
            if (stringValue.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && stringValue.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            }

            return stringValue.split(SYMBOL_COMMA);
        }

        return new Object[]{value};
    }

    @Override
    public Class<Object[]> getType() {
        return Object[].class;
    }

}
