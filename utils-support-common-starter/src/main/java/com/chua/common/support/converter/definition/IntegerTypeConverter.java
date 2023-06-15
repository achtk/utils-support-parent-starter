package com.chua.common.support.converter.definition;

import com.chua.common.support.utils.CollectionUtils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * Integer类型转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/10/30
 */
public class IntegerTypeConverter implements TypeConverter<Integer> {

    @Override
    public Integer convert(Object value) {
        if (null == value) {
            return null;
        }

        if (value.getClass().isArray()) {
            value = Array.get(value, 0);
        }

        if (value instanceof Collection) {
            value = CollectionUtils.findFirst((Collection) value);
        }

        BigDecimal bigDecimal = transToBigDecimal(value);
        if (null != bigDecimal) {
            return bigDecimal.intValue();
        }
        return convertIfNecessary(value);
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
