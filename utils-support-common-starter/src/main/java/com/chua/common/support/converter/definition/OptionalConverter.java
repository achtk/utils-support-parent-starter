package com.chua.common.support.converter.definition;

import java.util.Optional;

/**
 * Optional类型转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/26
 */
public class OptionalConverter implements TypeConverter<Optional> {

    @Override
    public Class<Optional> getType() {
        return Optional.class;
    }

    @Override
    public Optional convert(Object value) {
        return Optional.ofNullable(value);
    }
}
