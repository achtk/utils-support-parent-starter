package com.chua.common.support.converter.definition;

import com.chua.common.support.function.Splitter;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.TypeReference;

import java.util.*;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * Map转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/5
 */
public class MapTypeConverter implements TypeConverter<Map> {


    public static final MapTypeConverter INSTANCE = new MapTypeConverter();

    @Override
    @SuppressWarnings("ALL")
    public Map convert(Object value) {
        if (null == value) {
            return Collections.emptyMap();
        }

        if (isAssignableFrom(value, Dictionary.class)) {
            Dictionary dictionary = (Dictionary) value;
            Map item = new HashMap<>(1 << 4);
            Enumeration enumeration = dictionary.keys();
            while (enumeration.hasMoreElements()) {
                Object element = enumeration.nextElement();
                item.put(element, dictionary.get(element));
            }

            return item;
        }

        if (isAssignableFrom(value, Map.class)) {
            return (Map) value;
        }

        if (isAssignableFrom(value, String.class)) {
            String string = value.toString().trim();
            if (string.startsWith(SYMBOL_LEFT_BIG_PARENTHESES) && string.endsWith(SYMBOL_RIGHT_BIG_PARENTHESES)) {
                try {
                    Map<String, Object> map = Json.fromJson(string, new TypeReference<Map<String, Object>>() {
                    });
                    if (null != map) {
                        return map;
                    }
                } catch (Exception ignore) {
                }
                StringBuilder stringBuilder = new StringBuilder(SYMBOL_LEFT_BIG_PARENTHESES);
                String substring = string.substring(1, string.length() - 1);
                List<String> strings = Splitter.on(SYMBOL_COMMA).trimResults().omitEmptyStrings().splitToList(substring);
                Map<String, Object> rs = new LinkedHashMap<>();
                for (String s : strings) {
                    int index = s.indexOf(SYMBOL_EQUALS);
                    if (index > -1) {
                        rs.put(s.substring(0, index), createValue(s, index));
                        continue;
                    }

                    index = s.indexOf(SYMBOL_COLON);
                    if (index > -1) {
                        rs.put(s.substring(0, index), createValue(s, index));
                    }
                }

                return rs;
            }
            if (string.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && string.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                try {
                    Map<String, Object> map = Json.fromJson(string.replace(SYMBOL_LEFT_SQUARE_BRACKET, SYMBOL_LEFT_BIG_PARENTHESES)
                            .replace(SYMBOL_RIGHT_SQUARE_BRACKET, SYMBOL_RIGHT_BIG_PARENTHESES), new TypeReference<Map<String, Object>>() {
                    });
                    if (null != map) {
                        return map;
                    }
                } catch (Exception ignored) {
                }
            }
            try {
                Map<String, Object> map = Json.fromJson(SYMBOL_LEFT_BIG_PARENTHESES + string + SYMBOL_RIGHT_BIG_PARENTHESES, new TypeReference<Map<String, Object>>() {
                });
                if (null != map) {
                    return map;
                }
            } catch (Throwable ignored) {
            }
            string = string.substring(1, string.length() - 1);
            try {
                Map<String, String> map = Splitter.on(',').withKeyValueSeparator("=").split(string);
                if (null != map) {
                    return map;
                }
            } catch (Exception ignored) {
            }
            try {
                return Splitter.on(',').withKeyValueSeparator(':').split(string);
            } catch (Exception ignored) {
            }
        }
        return convertIfNecessary(value);
    }

    private Object createValue(String s, int index) {
        String keyValue = s.substring(index + 1).trim();

        if (Json.isJson(keyValue)) {
            if (keyValue.startsWith(SYMBOL_LEFT_BIG_PARENTHESES)) {
                return this.convert(keyValue);
            }
            return ListTypeConverter.INSTANCE.convert(keyValue);
        }
        return keyValue;
    }

    @Override
    public Class<Map> getType() {
        return Map.class;
    }
}
