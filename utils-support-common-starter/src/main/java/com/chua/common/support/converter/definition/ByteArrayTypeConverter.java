package com.chua.common.support.converter.definition;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * Byte数组转化
 *
 * @author CH
 * @version 1.0.0
 */
public class ByteArrayTypeConverter implements TypeConverter<Byte[]> {


    @Override
    public Byte[] convert(Object value) {
        if (null == value) {
            return new Byte[0];
        }

        Byte[] bytes = transToArray(value, Byte.class);
        if (bytes.length != 0) {
            return bytes;
        }

        if (value instanceof String) {
            String stringValue = value.toString();
            if (stringValue.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && stringValue.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            }
            return transToArray(stringValue.split(SYMBOL_COMMA), Byte.class);
        }

        return new Byte[0];
    }

    @Override
    public Class<Byte[]> getType() {
        return Byte[].class;
    }

}
