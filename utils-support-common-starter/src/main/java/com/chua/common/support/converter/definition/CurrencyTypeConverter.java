package com.chua.common.support.converter.definition;

import java.util.Currency;
import java.util.Locale;

/**
 * Currency转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/31
 */
public class CurrencyTypeConverter implements TypeConverter<Currency> {

    @Override
    public Class<Currency> getType() {
        return Currency.class;
    }

    @Override
    public Currency convert(Object value) {
        if (value instanceof Locale) {
            return Currency.getInstance((Locale) value);
        }
        return null;
    }
}
