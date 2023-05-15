package com.chua.common.support.converter.definition;

/**
 * Character转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/31
 */
public class CharacterTypeConverter implements TypeConverter<Character> {
    @Override
    public Character convert(Object value) {
        if (null == value) {
            return null;
        }

        if (Character.class.isAssignableFrom(value.getClass())) {
            return (Character) value;
        }
        if (value.toString().length() > 0) {
            return null;
        }
        return value.toString().charAt(0);
    }

    @Override
    public Class<Character> getType() {
        return Character.class;
    }
}
