package com.chua.common.support.converter.definition;

import java.math.BigDecimal;

/**
 * BigDecimal转化
 *
 * @author CH
 * @version 1.0.0
 */
public class BigDecimalTypeConverter implements TypeConverter<BigDecimal> {

    @Override
    public BigDecimal convert(Object value) {
        if (null == value) {
            return null;
        }

        BigDecimal bigDecimal = transToBigDecimal(value);
        if (null != bigDecimal) {
            return bigDecimal;
        }
        return convertIfNecessary(value);
    }

    @Override
    public Class<BigDecimal> getType() {
        return BigDecimal.class;
    }
}
