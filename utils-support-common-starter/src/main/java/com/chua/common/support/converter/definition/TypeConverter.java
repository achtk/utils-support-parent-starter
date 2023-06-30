package com.chua.common.support.converter.definition;

import com.chua.common.support.function.Splitter;
import com.chua.common.support.lang.date.DateUtils;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.utils.StringUtils;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * 类型转String
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/10/30
 */
public interface TypeConverter<O> {

    String E = "e";
    Pattern NU = Pattern.compile("(f|F|d|D)");
    Map<String, Integer> MAPPING = new HashMap<>(10);

    /**
     * 初始化
     */
    static void initial() {
        MAPPING.put("B", 1);
        MAPPING.put("KB", 1024);
        MAPPING.put("K", 1024);
        MAPPING.put("MB", 1024 * 1024);
        MAPPING.put("M", 1024 * 1024);
        MAPPING.put("GB", 1024 * 1024 * 1024);
        MAPPING.put("G", 1024 * 1024 * 1024);
        MAPPING.put("PB", 1024 * 1024 * 1024 * 1024);
        MAPPING.put("P", 1024 * 1024 * 1024 * 1024);
    }

    /**
     * 转化类型
     *
     * @return O
     */
    Class<O> getType();

    /**
     * 转化类型
     *
     * @param value 数据
     * @return 解雇
     */
    O convert(Object value);

    /**
     * 是否是type子类
     *
     * @param value 值
     * @param type  类型
     * @return 是否是type子类
     */
    default boolean isAssignableFrom(Object value, Class<?> type) {
        return type.isAssignableFrom(value.getClass());
    }

    /**
     * 二次转化
     *
     * @param value 数据
     * @return 结果
     */
    default O convertIfNecessary(Object value) {
        return null;
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @param type  类型
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    default <T> T[] transToArray(List value, Class<T> type) {
        return ArrayUtils.transToArray(value, type);
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @param type  类型
     * @return 能够转化返回T[], 反之返回 null
     */
    default <T> T[] transToArray(Object[] value, Class<T> type) {
        return ArrayUtils.transToArray(value, type);
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @param type  类型
     * @return 能够转化返回T[], 反之返回 null
     */
    default <T> T[] transToArray(Object value, Class<T> type) {

        if (value instanceof Byte[]) {
            return transToArray((Byte[]) value, type);
        }

        if (value instanceof Long[]) {
            return transToArray((Long[]) value, type);
        }

        if (value instanceof Double[]) {
            return transToArray((Double[]) value, type);
        }

        if (value instanceof Float[]) {
            return transToArray((Float[]) value, type);
        }

        if (value instanceof Short[]) {
            return transToArray((Short[]) value, type);
        }

        if (value instanceof Integer[]) {
            return transToArray((Integer[]) value, type);
        }

        if (value instanceof Boolean[]) {
            return transToArray((Boolean[]) value, type);
        }

        if (value instanceof byte[]) {
            return transToArray((byte[]) value, type);
        }

        if (value instanceof long[]) {
            return transToArray((long[]) value, type);
        }

        if (value instanceof double[]) {
            return transToArray((double[]) value, type);
        }

        if (value instanceof float[]) {
            return transToArray((float[]) value, type);
        }

        if (value instanceof short[]) {
            return transToArray((short[]) value, type);
        }

        if (value instanceof int[]) {
            return transToArray((int[]) value, type);
        }

        if (value instanceof boolean[]) {
            return transToArray((boolean[]) value, type);
        }
        return (T[]) Array.newInstance(type, 0);
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @param type  类型
     * @return 能够转化返回T[], 反之返回 null
     */
    default <T> T[] transToArray(byte[] value, Class<T> type) {
        return ArrayUtils.transToArray(value, type);
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @param type  类型
     * @return 能够转化返回T[], 反之返回 null
     */
    default <T> T[] transToArray(long[] value, Class<T> type) {
        return ArrayUtils.transToArray(value, type);
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @param type  类型
     * @return 能够转化返回T[], 反之返回 null
     */
    default <T> T[] transToArray(boolean[] value, Class<T> type) {
        return ArrayUtils.transToArray(value, type);
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @param type  类型
     * @return 能够转化返回T[], 反之返回 null
     */
    default <T> T[] transToArray(short[] value, Class<T> type) {
        return ArrayUtils.transToArray(value, type);
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @param type  类型
     * @return 能够转化返回T[], 反之返回 null
     */
    default <T> T[] transToArray(int[] value, Class<T> type) {
        return ArrayUtils.transToArray(value, type);
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @param type  类型
     * @return 能够转化返回T[], 反之返回 null
     */
    default <T> T[] transToArray(double[] value, Class<T> type) {
        return ArrayUtils.transToArray(value, type);
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @param type  类型
     * @return 能够转化返回T[], 反之返回 null
     */
    default <T> T[] transToArray(float[] value, Class<T> type) {
        return ArrayUtils.transToArray(value, type);
    }

    /**
     * 转化为 BigDecimal
     *
     * @param value 待转化值
     * @return 能够转化返回{@link BigDecimal}, 反之返回 null
     */
    default BigDecimal transToBigDecimal(Object value) {
        if (isAssignableFrom(value, Number.class)) {
            return new BigDecimal(value.toString());
        }

        if (isAssignableFrom(value, Date.class)) {
            return new BigDecimal(((Date) value).getTime());
        }

        if (isAssignableFrom(value, Color.class)) {
            return new BigDecimal(((Color) value).getRGB());
        }

        if (isAssignableFrom(value, File.class)) {
            return new BigDecimal(((File) value).length());
        }

        try {
            if (isAssignableFrom(value, String.class)) {
                return stringTransToBigDecimal(value.toString());
            }
        } catch (Exception e) {
            return null;
        }

        if (isAssignableFrom(value, LocalDateTime.class)) {
            return new BigDecimal(DateUtils.toDate((LocalDateTime) value).getTime());
        }

        if (isAssignableFrom(value, LocalDate.class)) {
            return new BigDecimal(DateUtils.toDate((LocalDate) value).getTime());
        }

        if (isAssignableFrom(value, LocalTime.class)) {
            return new BigDecimal(DateUtils.toDate((LocalTime) value).getTime());
        }

        if (isAssignableFrom(value, byte[].class)) {
            try {
                BigInteger bigInteger = new BigInteger((byte[]) value);
                return new BigDecimal(bigInteger);
            } catch (Exception ignored) {
            }
        }

        if (isAssignableFrom(value, char[].class)) {
            try {
                return new BigDecimal((char[]) value);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    /**
     * 字符串转BigDecimal
     *
     * @param value 字符串
     * @return BigDecimal
     */
    static BigDecimal stringTransToBigDecimal(String value) {
        if (NumberUtils.isNumber(value)) {
            return new BigDecimal(value);
        }

        String valueStr = value.toString();
        if (NumberUtils.isNumber(valueStr)) {
            return NumberUtils.converterNumber(valueStr, BigDecimal.class);
        }


        int size = 0;
        if (0 != (size = isSize(valueStr))) {
            String newValue = clearSize(valueStr);
            if (NumberUtils.isNumber(newValue)) {
                return NumberUtils.toBigDecimal(newValue).multiply(BigDecimal.valueOf(size));
            }
        }

        try {
            if (NumberUtils.isNumber(valueStr)) {
                return new BigDecimal(NumberUtils.toBigInteger(valueStr));
            }
            return new BigDecimal(NumberUtils.getNumberFromChinese(valueStr));
        } catch (Exception ignored) {
        }

        List<String> emptyStrings = Splitter.on(NU).splitToList(value);
        if (!emptyStrings.isEmpty()) {
            String toString = CollectionUtils.findFirst(emptyStrings);
            try {
                return BigDecimal.valueOf(Double.parseDouble(toString));
            } catch (NumberFormatException ignore) {
            }
        }

        if (NumberUtils.isDecimals(value)) {
            try {
                if (value.contains(E)) {
                    return new BigDecimal(value);
                } else {
                    return BigDecimal.valueOf(Double.parseDouble(value));
                }
            } catch (Exception ignore) {
            }
        }
        try {
            return NumberUtils.converterNumber(value, BigDecimal.class);
        } catch (Exception ignored) {
        }
        return null;
    }


    /**
     * 清除大小
     *
     * @param valueStr 值
     * @return 清除大小
     */
    static String clearSize(String valueStr) {
        if (MAPPING.isEmpty()) {
            initial();
        }

        String upperCase = valueStr.toUpperCase();
        for (Map.Entry<String, Integer> entry : MAPPING.entrySet()) {
            if (upperCase.endsWith(entry.getKey())) {
                return StringUtils.endWithMove(valueStr, entry.getKey());
            }
        }
        return valueStr;
    }

    /**
     * 是否是大小
     *
     * @param valueStr 值
     * @return 是否是大小
     */
    static int isSize(String valueStr) {
        if (MAPPING.isEmpty()) {
            initial();
        }

        String upperCase = valueStr.toUpperCase();
        for (Map.Entry<String, Integer> entry : MAPPING.entrySet()) {
            if (upperCase.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return 0;
    }
}
