package com.chua.common.support.converter;

import com.chua.common.support.converter.definition.EnumTypeConverter;
import com.chua.common.support.converter.definition.MapTypeConverter;
import com.chua.common.support.converter.definition.TypeConverter;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.spi.finder.SamePackageServiceFinder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.chua.common.support.utils.ClassUtils.fromPrimitive;

/**
 * 转换器
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/19
 */
@SuppressWarnings("ALL")
public final class Converter {

    private static final Map<Class<?>, TypeConverter> CONVERTER_MAP = new ConcurrentHashMap<>();

    private static final EnumTypeConverter ENUM_TYPE_CONVERTER = new EnumTypeConverter();
    private static final MapTypeConverter MAP_TYPE_CONVERTER = new MapTypeConverter();
    private static final Pattern PATTERN = Pattern.compile("(.*?)\\[(.*?)\\](.*?)");
    private static final Map<Class<?>, Object> DEFAULT_VALUE = new HashMap<Class<?>, Object>(8) {
        {
            put(long.class, 0L);
            put(short.class, (short) 0);
            put(byte.class, (byte) 0);
            put(float.class, 0F);
            put(double.class, 0D);
            put(boolean.class, false);
            put(char.class, ' ');
            put(int.class, 0);
        }
    };

    static {
        ServiceProvider<TypeConverter> serviceProvider = ServiceProvider.of(TypeConverter.class);
        serviceProvider.forEach((n, typeConverter) -> {
            CONVERTER_MAP.put(typeConverter.getType(), typeConverter);
        });
    }
    /**
     * 转化
     *
     * @param value 数据
     * @param type  类型
     * @param <E>   结果类型
     * @return 结果
     */
    public static Object convertIfNecessary(Object value, Type type) {
        if (type instanceof Class) {
            return convertIfNecessary(value, (Class<?>) type);
        }
        return null;
    }

    /**
     * 转化
     *
     * @param value 数据
     * @param type  类型
     * @param <E>   结果类型
     * @return 结果
     */
    public static <E> E convertIfNecessary(Object value, Class<E> type) {
        if (null == value) {
            return null;
        }

        Class<?> newType = convertIfPrimitive(type);

        if (newType.isAssignableFrom(value.getClass())) {
            return (E) value;
        }

        if (CONVERTER_MAP.containsKey(newType)) {
            E convert = (E) CONVERTER_MAP.get(newType).convert(value);
            if (type.isPrimitive() && null == convert) {
                return createDefaultPrimitive(type);
            }
            return convert;
        }

        if (newType.isEnum()) {
            return (E) ENUM_TYPE_CONVERTER.convertFor(value, newType);
        }

        return null;
    }

    /**
     * 默认值
     *
     * @param <E> 类型
     * @return 默认值
     */
    private static <E> E createDefaultPrimitive(Class<?> type) {
        return (E) DEFAULT_VALUE.get(type);
    }

    /**
     * 基础类转封装类
     *
     * @param target 类
     * @param <T>    类型
     * @return 封装类
     */
    public static <T> Class<T> convertIfPrimitive(Class<T> target) {
        if (!target.isPrimitive()) {
            return target;
        }
        return fromPrimitive(target);
    }

    /**
     * 转化Int
     * @param value 值
     * @return 结果
     */
    public static Integer createInteger(String value) {
        return Converter.convertIfNecessary(value, Integer.class);
    }
}
