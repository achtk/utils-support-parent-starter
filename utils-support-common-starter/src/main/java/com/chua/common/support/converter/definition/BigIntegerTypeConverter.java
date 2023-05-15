package com.chua.common.support.converter.definition;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * BigDecimal转化
 *
 * @author CH
 * @version 1.0.0
 */
public class BigIntegerTypeConverter implements TypeConverter<BigInteger> {

    @Override
    public BigInteger convert(Object value) {
        if (null == value) {
            return null;
        }

        BigDecimal bigDecimal = transToBigDecimal(value);
        if (null != bigDecimal) {
            return BigInteger.valueOf(bigDecimal.longValue());
        }
        return convertIfNecessary(value);
    }


    @Override
    public Class<BigInteger> getType() {
        return BigInteger.class;
    }
}
