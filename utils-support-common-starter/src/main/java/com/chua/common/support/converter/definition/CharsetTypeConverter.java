package com.chua.common.support.converter.definition;

import java.nio.charset.Charset;

/**
 * Charset 转化
 * <br />默认返回: UTF-8
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/31
 */
public class CharsetTypeConverter implements TypeConverter<Charset> {

    @Override
    public Class<Charset> getType() {
        return Charset.class;
    }

    @Override
    public Charset convert(Object value) {
        if (value instanceof String) {
            try {
                return Charset.forName(value.toString());
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
