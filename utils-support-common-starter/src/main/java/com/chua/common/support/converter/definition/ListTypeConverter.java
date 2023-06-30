package com.chua.common.support.converter.definition;

import com.chua.common.support.function.Splitter;
import com.chua.common.support.json.Json;
import com.chua.common.support.utils.ArrayUtils;

import java.util.*;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 字符串转化
 *
 * @author CH
 * @version 1.0.0
 */
public class ListTypeConverter implements TypeConverter<List> {


    public static final ListTypeConverter INSTANCE = new ListTypeConverter();

    @Override
    @SuppressWarnings("ALL")
    public List convert(Object value) {
        if (null == value) {
            return null;
        }

        if (isAssignableFrom(value, List.class)) {
            return (List) value;
        }

        if (isAssignableFrom(value, Collection.class)) {
            return new ArrayList((Collection) value);
        }

        if(value instanceof Iterable) {
            List<Object> tpl = new LinkedList<>();
            ((Iterable<?>) value).forEach(it -> {
                tpl.add(it);
            });
            return tpl;
        }

        if (isAssignableFrom(value, String.class)) {
            String string = value.toString().trim();
            if (string.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && string.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                try {
                    List rValue = Json.fromJson(string, List.class);
                    if (null != rValue) {
                        return rValue;
                    }
                } catch (Throwable ignored) {
                }
                boolean isListMap = string.startsWith("[{");
                Splitter splitter = Splitter.on(SYMBOL_COMMA).omitEmptyStrings().trimResults();
                String substring = string.substring(1, string.length() - 1);
                if (isListMap) {
                    String[] split = substring.split(",");
                    List<Map<String, Object>> result = new LinkedList<>();
                    for (String s : split) {
                        result.add(new MapTypeConverter().convert(s));
                    }
                    return result;
                } else {
                    return splitter.splitToList(substring);
                }
            }
            try {
                return Splitter.on(SYMBOL_COMMA).omitEmptyStrings().trimResults().splitToList(string);
            } catch (Exception ignored) {
            }
        }

        if (value.getClass().isArray()) {
            return (List) ArrayUtils.toList(value);
        }

        return convertIfNecessary(value);
    }

    @Override
    public Class<List> getType() {
        return List.class;
    }
}
