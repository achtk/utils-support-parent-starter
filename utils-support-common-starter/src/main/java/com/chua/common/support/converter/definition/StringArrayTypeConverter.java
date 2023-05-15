package com.chua.common.support.converter.definition;


import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.json.Json;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.*;


/**
 * 字符串数组转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/5
 */
public class StringArrayTypeConverter implements TypeConverter<String[]> {

    final public static Pattern PATTERN = Pattern.compile("\\}[\\s]{0,},[\\s]{0,}\\{");

    @Override
    public String[] convert(Object value) {
        if (null == value) {
            return new String[0];
        }

        if (value instanceof String[]) {
            return (String[]) value;
        }

        if (value instanceof String) {
            String stringValue = value.toString();
            if (stringValue.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && stringValue.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                try {
                    return transToArray(Json.fromJson(stringValue, List.class), String.class);
                } catch (Exception ignored) {
                }
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            } else if (stringValue.startsWith(SYMBOL_LEFT_BIG_PARENTHESES) && stringValue.endsWith(SYMBOL_RIGHT_BIG_PARENTHESES)) {
                String[] split = PATTERN.split(stringValue);
                List<String> result = new ArrayList<>(split.length);
                for (String it : split) {
                    if (!it.startsWith(SYMBOL_LEFT_BIG_PARENTHESES)) {
                        it = SYMBOL_LEFT_BIG_PARENTHESES + it;
                    }
                    if (!it.endsWith(CommonConstant.SYMBOL_RIGHT_BIG_PARENTHESES)) {
                        it += CommonConstant.SYMBOL_RIGHT_BIG_PARENTHESES;
                    }
                    result.add(it);
                }

                return result.toArray(new String[0]);
            }

            if (value instanceof URL) {
                return new String[]{((URL) value).toExternalForm()};
            }

            if (value instanceof URL[]) {
                return Arrays.stream(((URL[]) value)).map(java.net.URL::toExternalForm).toArray(String[]::new);
            }

            return stringValue.split(SYMBOL_COMMA);
        }

        return new String[0];
    }

    @Override
    public Class<String[]> getType() {
        return String[].class;
    }
}
