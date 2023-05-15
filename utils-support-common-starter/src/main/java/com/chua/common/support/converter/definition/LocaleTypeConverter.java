package com.chua.common.support.converter.definition;

import java.util.Locale;

/**
 * Locale 转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/31
 */
public class LocaleTypeConverter implements TypeConverter<Locale> {

    @Override
    public Class<Locale> getType() {
        return Locale.class;
    }

    @Override
    public Locale convert(Object value) {
        return null;
    }
}
