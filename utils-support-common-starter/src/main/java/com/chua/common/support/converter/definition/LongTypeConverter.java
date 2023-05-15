package com.chua.common.support.converter.definition;

import java.math.BigDecimal;

/**
 * long类型转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/10/30
 */
public class LongTypeConverter implements TypeConverter<Long> {

    @Override
    public Long convert(Object value) {
        if (null == value) {
            return null;
        }

        BigDecimal bigDecimal = transToBigDecimal(value);
        if (null != bigDecimal) {
            return bigDecimal.longValue();
        }

        return convertIfNecessary(value);
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }
}
