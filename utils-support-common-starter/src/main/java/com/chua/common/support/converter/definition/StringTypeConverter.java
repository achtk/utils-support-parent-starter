package com.chua.common.support.converter.definition;


import com.chua.common.support.function.Joiner;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

/**
 * 字符串转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/5
 */
public class StringTypeConverter implements TypeConverter<String> {

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String convert(Object value) {
        if (null == value) {
            return null;
        }

        if (value instanceof Collection) {
            return Joiner.on(",").join(value);
        }

        if (value instanceof Map) {
            return Joiner.on("&").withKeyValueSeparator("=").join(value);
        }

        if (value.getClass().isArray()) {
            if (value instanceof byte[]) {
                return new String((byte[]) value, StandardCharsets.UTF_8);
            }

            if (value instanceof Object[] && Array.getLength(value) == 1) {
                Object o = Array.get(value, 0);
                if (o instanceof byte[]) {
                    return new String((byte[]) o, StandardCharsets.UTF_8);
                }
                if(o instanceof String) {
                    return (String) o;
                }
            }

            String join = Joiner.on(",").join((Object[]) value);
            return "".equalsIgnoreCase(join) ? null : join;
        }

        if (value instanceof InputStream) {
            try {
                return StringUtils.utf8Str(IoUtils.toByteArray((InputStream) value));
            } catch (IOException ignored) {
            }
        }
        return value.toString();
    }
}
