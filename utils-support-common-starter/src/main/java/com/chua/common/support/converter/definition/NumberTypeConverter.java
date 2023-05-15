package com.chua.common.support.converter.definition;

/**
 * Number 转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/31
 */
public class NumberTypeConverter implements TypeConverter<Number> {


    @Override
    public Class<Number> getType() {
        return Number.class;
    }

    @Override
    public Number convert(Object value) {
        return transToBigDecimal(value);
    }


}
