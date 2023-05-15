package com.chua.common.support.converter.definition;

import com.chua.common.support.function.Splitter;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.TypeReference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 字符串转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/5
 */
public class SetTypeConverter implements TypeConverter<Set> {
    public static final SetTypeConverter INSTANCE = new SetTypeConverter();

    @Override
    @SuppressWarnings("ALL")
    public Set convert(Object value) {
        if (null == value) {
            return null;
        }

        if (isAssignableFrom(value, Set.class)) {
            return (Set) value;
        }

        if (isAssignableFrom(value, Collection.class)) {
            return new HashSet((Collection) value);
        }

        if (isAssignableFrom(value, String.class)) {
            String string = value.toString().trim();
            if (string.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && string.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                try {
                    Set rValue = Json.fromJson(string, new TypeReference<Set<String>>() {
                    });
                    if (null != rValue) {
                        return rValue;
                    }
                } catch (Throwable ignored) {
                }
                try {
                    return new HashSet(Splitter.on(SYMBOL_COMMA).omitEmptyStrings().trimResults().splitToList(string.substring(1, string.length() - 1)));
                } catch (Exception ignored) {
                }
            }
            try {
                return new HashSet(Splitter.on(SYMBOL_COMMA).omitEmptyStrings().trimResults().splitToList(string));
            } catch (Exception ignored) {
            }
        }

        return convertIfNecessary(value);
    }

    @Override
    public Class<Set> getType() {
        return Set.class;
    }
}
