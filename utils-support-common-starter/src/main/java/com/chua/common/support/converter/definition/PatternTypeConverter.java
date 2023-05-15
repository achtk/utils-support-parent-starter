package com.chua.common.support.converter.definition;

import java.util.regex.Pattern;

/**
 * 正则
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/19
 */
public class PatternTypeConverter implements TypeConverter<Pattern> {

    @Override
    public Class<Pattern> getType() {
        return Pattern.class;
    }

    @Override
    public Pattern convert(Object value) {
        if (value instanceof String) {
            return Pattern.compile(value.toString());
        }
        return null;
    }
}
