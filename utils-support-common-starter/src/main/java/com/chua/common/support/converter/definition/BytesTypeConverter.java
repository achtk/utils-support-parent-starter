package com.chua.common.support.converter.definition;

import com.chua.common.support.utils.ArrayUtils;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * Byte数组转化
 *
 * @author CH
 * @version 1.0.0
 */
public class BytesTypeConverter implements TypeConverter<byte[]> {


    @Override
    public byte[] convert(Object value) {
        if (null == value) {
            return new byte[0];
        }

        byte[] bytes = ArrayUtils.transToByteArray(value);
        if (bytes.length != 0) {
            return bytes;
        }

        if (value instanceof String) {
            String stringValue = value.toString();
            if (stringValue.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && stringValue.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            }
            return ArrayUtils.transToByteArray(stringValue.split(SYMBOL_COMMA));
        }

        return new byte[0];
    }

    @Override
    public Class<byte[]> getType() {
        return byte[].class;
    }

}
