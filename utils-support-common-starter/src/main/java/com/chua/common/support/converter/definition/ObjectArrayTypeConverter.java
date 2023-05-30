package com.chua.common.support.converter.definition;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 字符串数组转化
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/5
 */
public class ObjectArrayTypeConverter implements TypeConverter<Object[]> {

    final public static Pattern PATTERN = Pattern.compile("\\}[\\s]{0,},[\\s]{0,}\\{");

    @Override
    public Object[] convert(Object value) {
        if (null == value) {
            return new Object[0];
        }

        if (value instanceof String[]) {
            return (String[]) value;
        }
        if (value instanceof Integer[]) {
            return (Integer[]) value;
        }
        if (value instanceof Boolean[]) {
            return (Boolean[]) value;
        }
        if (value instanceof Short[]) {
            return (Short[]) value;
        }
        if (value instanceof Long[]) {
            return (Long[]) value;
        }
        if (value instanceof Float[]) {
            return (Float[]) value;
        }
        if (value instanceof Byte[]) {
            return (Byte[]) value;
        }
        if (value instanceof Double[]) {
            return (Double[]) value;
        }

        if (value instanceof String) {
            String stringValue = value.toString();
            if (stringValue.startsWith(SYMBOL_LEFT_SQUARE_BRACKET) && stringValue.endsWith(SYMBOL_RIGHT_SQUARE_BRACKET)) {
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            }

            return stringValue.split(SYMBOL_COMMA);
        }

        if (value instanceof Collection) {
            return ((Collection<?>) value).toArray();
        }

        return new Object[]{value};
    }

    @Override
    public Class<Object[]> getType() {
        return Object[].class;
    }

    @SuppressWarnings("ALL")
    public <T> T convertFor(Object value, Class<T> newType) {
        if (null == value) {
            return (T) Array.newInstance(newType, 0);
        }

        List tpl = new LinkedList();
        Class<?> actualType = ClassUtils.getActualType(newType);
        if (value instanceof Collection) {
            ((Collection<?>) value).forEach(it -> {
                tpl.add(BeanUtils.copyProperties(it, actualType));
            });

            return (T) ArrayUtils.toArray(tpl);
        }

        if (value instanceof Map) {
            ((Map) value).values().forEach(it -> {
                tpl.add(BeanUtils.copyProperties(it, actualType));
            });

            return (T) ArrayUtils.toArray(tpl);
        }

        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object o = Array.get(value, i);
                tpl.add(BeanUtils.copyProperties(o, actualType));
            }

            return (T) ArrayUtils.toArray(tpl);
        }

        Object newInstance = Array.newInstance(actualType, 1);
        Object[] ts = new Object[]{BeanUtils.copyProperties(value, actualType)};
        System.arraycopy(ts, 0, newInstance, 0, 1);
        return (T) newInstance;
    }
}
