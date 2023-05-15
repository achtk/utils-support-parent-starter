package com.chua.common.support.converter.definition;

/**
 * 正则
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/19
 */
public class EnumTypeConverter implements TypeConverter<Enum> {

    @Override
    public Enum convert(Object value) {
        if (null == value) {
            return null;
        }

        if (value instanceof Enum) {
            return (Enum) value;
        }

        return convertIfNecessary(value);
    }

    @Override
    public Class<Enum> getType() {
        return Enum.class;
    }

    public <T> T convertFor(Object value, Class<T> newType) {
        if (null == value) {
            return null;
        }

        T[] enumConstants = newType.getEnumConstants();
        for (T enumConstant : enumConstants) {
            if (enumConstant.toString().equalsIgnoreCase(value.toString())) {
                return enumConstant;
            }
        }
        return null;
    }

}
