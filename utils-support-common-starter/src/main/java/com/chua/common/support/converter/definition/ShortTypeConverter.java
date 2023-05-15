package com.chua.common.support.converter.definition;

import java.math.BigDecimal;

/**
 * Short类型转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/10/30
 */
public class ShortTypeConverter implements TypeConverter<Short> {

    @Override
    public Short convert(Object value) {
        if (null == value) {
            return null;
        }

        BigDecimal bigDecimal = transToBigDecimal(value);
        if (null != bigDecimal) {
            return bigDecimal.shortValue();
        }
        return null;
    }

    @Override
    public Class<Short> getType() {
        return Short.class;
    }
}
