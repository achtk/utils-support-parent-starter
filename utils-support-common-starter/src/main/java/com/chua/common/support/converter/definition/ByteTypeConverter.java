package com.chua.common.support.converter.definition;

import java.math.BigDecimal;

/**
 * Double类型转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/10/30
 */
public class ByteTypeConverter implements TypeConverter<Byte> {

    @Override
    public Class<Byte> getType() {
        return Byte.class;
    }

    @Override
    public Byte convert(Object value) {
        BigDecimal bigDecimal = transToBigDecimal(value);
        if (null != bigDecimal) {
            return bigDecimal.byteValue();
        }
        return convertIfNecessary(value);
    }
}
