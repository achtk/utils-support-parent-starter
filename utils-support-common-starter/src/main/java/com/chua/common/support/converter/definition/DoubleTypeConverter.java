package com.chua.common.support.converter.definition;

import java.math.BigDecimal;

/**
 * Double类型转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/10/30
 */
public class DoubleTypeConverter implements TypeConverter<Double> {

    @Override
    public Double convert(Object value) {
        if (null == value) {
            return null;
        }

        BigDecimal bigDecimal = transToBigDecimal(value);
        if (null != bigDecimal) {
            return bigDecimal.doubleValue();
        }
        return convertIfNecessary(value);
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}
