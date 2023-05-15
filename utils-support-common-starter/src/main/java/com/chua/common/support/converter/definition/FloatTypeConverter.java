package com.chua.common.support.converter.definition;

import java.math.BigDecimal;

/**
 * Float类型转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/10/30
 */
public class FloatTypeConverter implements TypeConverter<Float> {

    @Override
    public Float convert(Object value) {
        if (null == value) {
            return null;
        }

        BigDecimal bigDecimal = transToBigDecimal(value);
        if (null != bigDecimal) {
            return bigDecimal.floatValue();
        }
        return convertIfNecessary(value);
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }
}
