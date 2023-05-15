package com.chua.common.support.utils;

import com.chua.common.support.value.NumberValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.constant.RegexConstant.INT_PATTERN;


/**
 * 数字处理
 *
 * @author Administrator
 */
public class NumberUtils {

    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);

    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    private static final int A = 'A';
    private static final int Z = 'Z';

    /**
     * 获取随机数据
     *
     * @param start 开始
     * @param end   结束
     * @return 随机数
     */
    public static int getNum(int start, int end) {
        return (int) (Math.random() * (end - start + 1) + start);
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static double add(float v1, float v2) {
        return add(Float.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static double add(float v1, double v2) {
        return add(Float.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static double add(double v1, float v2) {
        return add(Double.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static double add(double v1, double v2) {
        return add(Double.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     * @since 3.1.1
     */
    public static double add(Double v1, Double v2) {
                return add((Number) v1, (Number) v2).doubleValue();
    }

    /**
     * 提供精确的加法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static BigDecimal add(Number v1, Number v2) {
        return add(new Number[]{v1, v2});
    }

    /**
     * 提供精确的加法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被加值
     * @return 和
     * @since 4.0.0
     */
    public static BigDecimal add(Number... values) {
        if (ArrayUtils.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        Number value = values[0];
        BigDecimal result = null == value ? BigDecimal.ZERO : new BigDecimal(value.toString());
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (null != value) {
                result = result.add(new BigDecimal(value.toString()));
            }
        }
        return result;
    }

    /**
     * 提供精确的加法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被加值
     * @return 和
     * @since 4.0.0
     */
    public static BigDecimal add(String... values) {
        if (ArrayUtils.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        String value = values[0];
        BigDecimal result = null == value ? BigDecimal.ZERO : new BigDecimal(value);
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (null != value) {
                result = result.add(new BigDecimal(value));
            }
        }
        return result;
    }

    /**
     * 提供精确的加法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被加值
     * @return 和
     * @since 4.0.0
     */
    public static BigDecimal add(BigDecimal... values) {
        if (ArrayUtils.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        BigDecimal value = values[0];
        BigDecimal result = null == value ? BigDecimal.ZERO : value;
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (null != value) {
                result = result.add(value);
            }
        }
        return result;
    }

    /**
     * 补充Math.ceilDiv() JDK8中添加了和Math.floorDiv()但却没有ceilDiv()
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     * @since 5.3.3
     */
    public static int ceilDiv(int v1, int v2) {
        return (int) Math.ceil((double) v1 / v2);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(float v1, float v2) {
        return div(v1, v2, 10);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(float v1, double v2) {
        return div(v1, v2, 10);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1, float v2) {
        return div(v1, v2, 10);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, 10);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(Double v1, Double v2) {
        return div(v1, v2, 10);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     * @since 3.1.0
     */
    public static BigDecimal div(Number v1, Number v2) {
        return div(v1, v2, 10);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static BigDecimal div(String v1, String v2) {
        return div(v1, v2, 10);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static double div(float v1, float v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static double div(float v1, double v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static double div(double v1, float v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static double div(Double v1, Double v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     * @since 3.1.0
     */
    public static BigDecimal div(Number v1, Number v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度，如果为负值，取绝对值
     * @return 两个参数的商
     */
    public static BigDecimal div(String v1, String v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static double div(float v1, float v2, int scale, RoundingMode roundingMode) {
        return div(Float.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static double div(float v1, double v2, int scale, RoundingMode roundingMode) {
        return div(Float.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static double div(double v1, float v2, int scale, RoundingMode roundingMode) {
        return div(Double.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale, RoundingMode roundingMode) {
        return div(Double.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static double div(Double v1, Double v2, int scale, RoundingMode roundingMode) {
        return div((Number) v1, (Number) v2, scale, roundingMode).doubleValue();
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     * @since 3.1.0
     */
    public static BigDecimal div(Number v1, Number v2, int scale, RoundingMode roundingMode) {
        return div(v1.toString(), v2.toString(), scale, roundingMode);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     */
    public static BigDecimal div(String v1, String v2, int scale, RoundingMode roundingMode) {
        return div(new BigDecimal(v1), new BigDecimal(v2), scale, roundingMode);
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度，如果为负值，取绝对值
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 两个参数的商
     * @since 3.0.9
     */
    public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale, RoundingMode roundingMode) {
        if (null == v1) {
            return BigDecimal.ZERO;
        }
        if (scale < 0) {
            scale = -scale;
        }
        return v1.divide(v2, scale, roundingMode);
    }

    /**
     * 计算比率。计算结果四舍五入。
     *
     * @param numerator   分子
     * @param denominator 分母
     * @param scale       保留小数点后位数
     * @return 比率
     */
    public static double divide(long numerator, long denominator, int scale) {
        BigDecimal numeratorBd = new BigDecimal(numerator);
        BigDecimal denominatorBd = new BigDecimal(denominator);
        return numeratorBd.divide(denominatorBd, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 计算比率。计算结果四舍五入。保留小数点后两位。
     *
     * @param numerator   分子
     * @param denominator 分母
     * @return 比率
     */
    public static double divide(long numerator, long denominator) {
        return divide(numerator, denominator, 2);
    }

    /**
     * 计算比率。计算结果四舍五入。
     *
     * @param numerator   分子
     * @param denominator 分母
     * @param scale       保留小数点后位数
     * @return 比率
     */
    public static double divide(double numerator, double denominator, int scale) {
        BigDecimal numeratorBd = BigDecimal.valueOf(numerator);
        BigDecimal denominatorBd = BigDecimal.valueOf(denominator);
        return numeratorBd.divide(denominatorBd, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 计算比率。计算结果四舍五入。保留小数点后两位。
     *
     * @param numerator   分子
     * @param denominator 分母
     * @return 比率
     */
    public static double divide(double numerator, double denominator) {
        return divide(numerator, denominator, 2);
    }

    /**
     * 最大公约数
     *
     * @param m 第一个值
     * @param n 第二个值
     * @return 最大公约数
     */
    public static int divisor(int m, int n) {
        while (m % n != 0) {
            int temp = m % n;
            m = n;
            n = temp;
        }
        return n;
    }

    /**
     * 计算阶乘
     * <p>
     * n! = n * (n-1) * ... * end
     * </p>
     *
     * @param start 阶乘起始
     * @param end   阶乘结束，必须小于起始
     * @return 结果
     * @since 4.1.0
     */
    public static long factorial(long start, long end) {
        if (0L == start || start == end) {
            return 1L;
        }
        if (start < end) {
            return 0L;
        }
        return start * factorial(start - 1, end);
    }

    /**
     * 计算阶乘
     * <p>
     * n! = n * (n-1) * ... * 2 * 1
     * </p>
     *
     * @param n 阶乘起始
     * @return 结果
     */
    public static long factorial(long n) {
        return factorial(n, 1);
    }

    /**
     * 栅格
     *
     * @param max 总大小
     * @param min 每块大小
     * @return int
     */
    public static int fences(long max, long min) {
        final Long before = max / min;
        final Long after = max % min == 0L ? 0L : 1L;
        final long sum = (before + after);
        return (int) sum;
    }

    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     *
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isDecimals(String decimals) {
        return Pattern.matches(DECIMALS.pattern(), decimals);
    }

    /**
     * 验证整数（正整数和负整数）
     *
     * @param digit 一位或多位0-9之间的整数
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isDigit(String digit) {
        return Pattern.matches(DIGIT.pattern(), digit);
    }

    /**
     * is integer string.
     *
     * @param str 字符串
     * @return is integer
     */
    public static boolean isInteger(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return INT_PATTERN.matcher(str).matches();
    }

    /**
     * 是否是数字
     *
     * @param str 原始数据
     * @return boolean
     */
    public static boolean isNumber(final CharSequence str) {
        if (null == str) {
            return false;
        }

        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static double mul(float v1, float v2) {
        return mul(Float.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static double mul(float v1, double v2) {
        return mul(Float.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static double mul(double v1, float v2) {
        return mul(Double.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static double mul(double v1, double v2) {
        return mul(Double.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的乘法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static double mul(Double v1, Double v2) {
        return mul((Number) v1, (Number) v2).doubleValue();
    }

    /**
     * 提供精确的乘法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static BigDecimal mul(Number v1, Number v2) {
        return mul(new Number[]{v1, v2});
    }

    /**
     * 提供精确的乘法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被乘值
     * @return 积
     * @since 4.0.0
     */
    public static BigDecimal mul(Number... values) {
        if (ArrayUtils.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        Number value = values[0];
        BigDecimal result = new BigDecimal(value.toString());
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            result = result.multiply(new BigDecimal(value.toString()));
        }
        return result;
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     * @since 3.0.8
     */
    public static BigDecimal mul(String v1, String v2) {
        return mul(new BigDecimal(v1), new BigDecimal(v2));
    }

    /**
     * 提供精确的乘法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被乘值
     * @return 积
     * @since 4.0.0
     */
    public static BigDecimal mul(String... values) {
        if (ArrayUtils.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        BigDecimal result = new BigDecimal(values[0]);
        for (int i = 1; i < values.length; i++) {
            result = result.multiply(new BigDecimal(values[i]));
        }

        return result;
    }

    /**
     * 提供精确的乘法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被乘值
     * @return 积
     * @since 4.0.0
     */
    public static BigDecimal mul(BigDecimal... values) {
        if (ArrayUtils.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        BigDecimal result = values[0];
        for (int i = 1; i < values.length; i++) {
            result = result.multiply(values[i]);
        }
        return result;
    }

    /**
     * 最小公倍数
     *
     * @param m 第一个值
     * @param n 第二个值
     * @return 最小公倍数
     */
    public static int multiple(int m, int n) {
        return m * n / divisor(m, n);
    }

    /**
     * 将int整数与小数相乘，计算结四舍五入保留整数位。
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 数字相乘计算结果
     */
    public static int multiply(int num1, double num2) {
        return multiply((double) num1, num2);
    }

    /**
     * 将long整数与小数相乘，计算结四舍五入保留整数位。
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 数字相乘计算结果
     */
    public static int multiply(long num1, double num2) {
        double num1D = ((Long) num1).doubleValue();
        return multiply(num1D, num2);
    }

    /**
     * 将double与小数相乘，计算结四舍五入保留整数位。
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 数字相乘计算结果
     */
    public static int multiply(double num1, double num2) {
        BigDecimal num1Bd = BigDecimal.valueOf(num1);
        BigDecimal num2Bd = BigDecimal.valueOf(num2);
        MathContext mathContext = new MathContext(num1Bd.precision(), RoundingMode.HALF_UP);
        return num1Bd.multiply(num2Bd, mathContext).intValue();
    }

    /**
     * String转化为Number
     *
     * @param <T>         类型
     * @param text        数据
     * @param targetClass 目标类型
     * @return Number
     */
    @SuppressWarnings("all")
    public static <T extends Number> T parseNumber(Number number, Class<T> targetClass) {
        if (targetClass.isInstance(number)) {
            return (T) number;
        } else if (Byte.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Byte.valueOf(number.byteValue());
        } else if (Short.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Short.valueOf(number.shortValue());
        } else if (Integer.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Integer.valueOf(number.intValue());
        } else if (Long.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            return (T) Long.valueOf(value);
        } else if (BigInteger.class == targetClass) {
            if (number instanceof BigDecimal) {
                return (T) ((BigDecimal) number).toBigInteger();
            } else {
                return (T) BigInteger.valueOf(number.longValue());
            }
        } else if (Float.class == targetClass) {
            return (T) Float.valueOf(number.floatValue());
        } else if (Double.class == targetClass) {
            return (T) Double.valueOf(number.doubleValue());
        } else if (BigDecimal.class == targetClass) {
            return (T) new BigDecimal(number.toString());
        } else {
            throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" +
                    number.getClass().getName() + "] to unsupported target class [" + targetClass.getName() + "]");
        }
    }

    /**
     * Raise an <em>overflow</em> exception for the given number and target class.
     *
     * @param number      the number we tried to convert
     * @param targetClass the target class we tried to convert to
     * @throws IllegalArgumentException if there is an overflow
     */
    private static void raiseOverflowException(Number number, Class<?> targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" +
                number.getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
    }

    /**
     * Check for a {@code BigInteger}/{@code BigDecimal} long overflow
     * before returning the given number as a long value.
     *
     * @param number      the number to convert
     * @param targetClass the target class to convert to
     * @return the long value, if convertible without overflow
     * @throws IllegalArgumentException if there is an overflow
     * @see #raiseOverflowException
     */
    private static long checkedLongValue(Number number, Class<? extends Number> targetClass) {
        BigInteger bigInt = null;
        if (number instanceof BigInteger) {
            bigInt = (BigInteger) number;
        } else if (number instanceof BigDecimal) {
            bigInt = ((BigDecimal) number).toBigInteger();
        }
        boolean b = bigInt != null && (bigInt.compareTo(LONG_MIN) < 0 || bigInt.compareTo(LONG_MAX) > 0);
        if (b) {
            raiseOverflowException(number, targetClass);
        }
        return number.longValue();
    }

    /**
     * String转化为Number
     *
     * @param <T>         类型
     * @param text        数据
     * @param targetClass 目标类型
     * @return Number
     */
    @SuppressWarnings("all")
    public static <T extends Number> T converterNumber(String text, Class<T> targetClass) {
        String trimmed = StringUtils.trimAllWhitespace(text);

        if (Byte.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
        } else if (Short.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
        } else if (Integer.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
        } else if (Long.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
        } else if (BigInteger.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed));
        } else if (Float.class == targetClass) {
            return (T) Float.valueOf(trimmed);
        } else if (Double.class == targetClass) {
            return (T) Double.valueOf(trimmed);
        } else if (BigDecimal.class == targetClass || Number.class == targetClass) {
            return (T) new BigDecimal(trimmed);
        } else {
            throw new IllegalArgumentException(
                    "Cannot convert String [" + text + "] to target class [" + targetClass.getName() + "]");
        }
    }

    /**
     * 把给定的总数平均分成N份，返回每份的个数<br>
     * 当除以分数有余数时每份+1
     *
     * @param total     总数
     * @param partCount 份数
     * @return 每份的个数
     * @since 4.0.7
     */
    public static int partValue(int total, int partCount) {
        return partValue(total, partCount, true);
    }

    /**
     * 把给定的总数平均分成N份，返回每份的个数<br>
     * 如果isPlusOneWhenHasRem为true，则当除以分数有余数时每份+1，否则丢弃余数部分
     *
     * @param total               总数
     * @param partCount           份数
     * @param isPlusOneWhenHasRem 在有余数时是否每份+1
     * @return 每份的个数
     * @since 4.0.7
     */
    public static int partValue(int total, int partCount, boolean isPlusOneWhenHasRem) {
        int partValue = total / partCount;
        if (isPlusOneWhenHasRem && total % partCount == 0) {
            partValue++;
        }
        return partValue;
    }

    /**
     * 百分比
     *
     * @param current 当前
     * @param total   最大
     * @return 百分比
     */
    public static double percentage(double current, double total) {
        return current / total * 100.;
    }

    /**
     * 获取整型流
     *
     * @param start 开始位置
     * @param end   结束位置
     * @return 流
     * @see IntStream
     */
    public static IntStream range(int start, int end) {
        return IntStream.range(start, end);
    }

    /**
     * 获取整型流
     *
     * @param end 结束位置
     * @return 流
     * @see IntStream
     */
    public static IntStream range(int end) {
        return IntStream.range(0, end);
    }

    /**
     * 获取整型流
     *
     * @param end      结束位置
     * @param consumer 消费者
     * @see IntStream
     */
    public static void range(int end, IntConsumer consumer) {
        IntStream.range(0, end).forEach(consumer);
    }

    /**
     * 获取整型流
     *
     * @param start    开始位置
     * @param end      结束位置
     * @param consumer 消费者
     * @see IntStream
     */
    public static void range(int start, int end, IntConsumer consumer) {
        IntStream.range(start, end).forEach(consumer);
    }

    /**
     * 获取整型流
     *
     * @param end      结束位置
     * @param consumer 消费者
     * @see IntStream
     */
    public static void rangeClosed(int end, IntConsumer consumer) {
        IntStream.rangeClosed(0, end).forEach(consumer);
    }

    /**
     * 获取整型流
     *
     * @param start 开始位置
     * @param end   结束位置
     * @return 流
     * @see IntStream
     */
    public static IntStream rangeClosed(int start, int end) {
        return IntStream.rangeClosed(start, end);
    }

    /**
     * 保留固定位数小数<br>
     * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
     * 例如保留2位小数：123.456789 =》 123.46
     *
     * @param v     值
     * @param scale 保留小数位数
     * @return 新值
     */
    public static BigDecimal round(double v, int scale) {
        return round(v, scale, RoundingMode.HALF_UP);
    }

    /**
     * 保留固定位数小数<br>
     * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
     * 例如保留2位小数：123.456789 =》 123.46
     *
     * @param numberStr 数字值的字符串表现形式
     * @param scale     保留小数位数
     * @return 新值
     */
    public static BigDecimal round(String numberStr, int scale) {
        return round(numberStr, scale, RoundingMode.HALF_UP);
    }

    /**
     * 保留固定位数小数<br>
     * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
     * 例如保留2位小数：123.456789 =》 123.46
     *
     * @param number 数字值
     * @param scale  保留小数位数
     * @return 新值
     * @since 4.1.0
     */
    public static BigDecimal round(BigDecimal number, int scale) {
        return round(number, scale, RoundingMode.HALF_UP);
    }

    /**
     * 保留固定位数小数<br>
     * 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param v            值
     * @param scale        保留小数位数
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 新值
     */
    public static BigDecimal round(double v, int scale, RoundingMode roundingMode) {
        return round(Double.toString(v), scale, roundingMode);
    }

    /**
     * 保留固定位数小数<br>
     * 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param numberStr    数字值的字符串表现形式
     * @param scale        保留小数位数，如果传入小于0，则默认0
     * @param roundingMode 保留小数的模式 {@link RoundingMode}，如果传入null则默认四舍五入
     * @return 新值
     */
    public static BigDecimal round(String numberStr, int scale, RoundingMode roundingMode) {
        if (scale < 0) {
            scale = 0;
        }
        return round(toBigDecimal(numberStr), scale, roundingMode);
    }

    /**
     * 保留固定位数小数<br>
     * 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param number       数字值
     * @param scale        保留小数位数，如果传入小于0，则默认0
     * @param roundingMode 保留小数的模式 {@link RoundingMode}，如果传入null则默认四舍五入
     * @return 新值
     */
    public static BigDecimal round(BigDecimal number, int scale, RoundingMode roundingMode) {
        if (null == number) {
            number = BigDecimal.ZERO;
        }
        if (scale < 0) {
            scale = 0;
        }
        if (null == roundingMode) {
            roundingMode = RoundingMode.HALF_UP;
        }

        return number.setScale(scale, roundingMode);
    }

    /**
     * 保留固定小数位数，舍去多余位数
     *
     * @param number 需要科学计算的数据
     * @param scale  保留的小数位
     * @return 结果
     * @since 4.1.0
     */
    public static BigDecimal roundDown(Number number, int scale) {
        return roundDown(toBigDecimal(number), scale);
    }

    /**
     * 保留固定小数位数，舍去多余位数
     *
     * @param value 需要科学计算的数据
     * @param scale 保留的小数位
     * @return 结果
     * @since 4.1.0
     */
    public static BigDecimal roundDown(BigDecimal value, int scale) {
        return round(value, scale, RoundingMode.DOWN);
    }

    /**
     * 四舍六入五成双计算法
     * <p>
     * 四舍六入五成双是一种比较精确比较科学的计数保留法，是一种数字修约规则。
     * </p>
     *
     * <pre>
     * 算法规则:
     * 四舍六入五考虑，
     * 五后非零就进一，
     * 五后皆零看奇偶，
     * 五前为偶应舍去，
     * 五前为奇要进一。
     * </pre>
     *
     * @param number 需要科学计算的数据
     * @param scale  保留的小数位
     * @return 结果
     * @since 4.1.0
     */
    public static BigDecimal roundHalfEven(Number number, int scale) {
        return roundHalfEven(toBigDecimal(number), scale);
    }

    /**
     * 四舍六入五成双计算法
     * <p>
     * 四舍六入五成双是一种比较精确比较科学的计数保留法，是一种数字修约规则。
     * </p>
     *
     * <pre>
     * 算法规则:
     * 四舍六入五考虑，
     * 五后非零就进一，
     * 五后皆零看奇偶，
     * 五前为偶应舍去，
     * 五前为奇要进一。
     * </pre>
     *
     * @param value 需要科学计算的数据
     * @param scale 保留的小数位
     * @return 结果
     * @since 4.1.0
     */
    public static BigDecimal roundHalfEven(BigDecimal value, int scale) {
        return round(value, scale, RoundingMode.HALF_EVEN);
    }

    /**
     * 保留固定位数小数<br>
     * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
     * 例如保留2位小数：123.456789 =》 123.46
     *
     * @param v     值
     * @param scale 保留小数位数
     * @return 新值
     */
    public static String roundStr(double v, int scale) {
        return round(v, scale).toString();
    }

    /**
     * 保留固定位数小数<br>
     * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
     * 例如保留2位小数：123.456789 =》 123.46
     *
     * @param numberStr 数字值的字符串表现形式
     * @param scale     保留小数位数
     * @return 新值
     * @since 3.2.2
     */
    public static String roundStr(String numberStr, int scale) {
        return round(numberStr, scale).toString();
    }

    /**
     * 保留固定位数小数<br>
     * 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param v            值
     * @param scale        保留小数位数
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 新值
     * @since 3.2.2
     */
    public static String roundStr(double v, int scale, RoundingMode roundingMode) {
        return round(v, scale, roundingMode).toString();
    }

    /**
     * 保留固定位数小数<br>
     * 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param numberStr    数字值的字符串表现形式
     * @param scale        保留小数位数
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 新值
     * @since 3.2.2
     */
    public static String roundStr(String numberStr, int scale, RoundingMode roundingMode) {
        return round(numberStr, scale, roundingMode).toString();
    }

    /**
     * 安全的Integer长度
     *
     * @param value 数据
     * @return 安全长度
     */
    public static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    /**
     * 获取小数点后{scale}数据
     *
     * @param value 值
     * @param scale 小数点位数
     * @return 数据
     */
    public static BigDecimal scale(Long value, int scale) {
        if (null == value) {
            return BigDecimal.ZERO;
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        return bigDecimal.setScale(scale < 0 ? 2 : scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 等分數據
     *
     * @param size     填充大小
     * @param realSize 實際大小
     * @param thread   個數
     * @return 数组
     */
    public static List<Map.Entry<Long, Long>> split(long size, long realSize, int thread) {
        List<Map.Entry<Long, Long>> result = new LinkedList<>();
        int cols = (int) (realSize / thread);

        if (cols == 0) {
            return Collections.emptyList();
        }
        int less = (int) (realSize % thread);
        IntStream.range(0, thread).forEach(it -> {
            Map<Long, Long> item = new HashMap<>(1);
            int less1 = less < thread ? 1 : 0;
            if (result.isEmpty()) {
                item.put(size + it * cols, Math.min(size + (it + 1) * cols + less1, realSize));
            } else if (it == thread - 1) {
                item.put(result.get(result.size() - 1).getValue() + 1, Math.min(size + (it + 1) * cols + less1 + less, realSize));
            } else {
                item.put(result.get(result.size() - 1).getValue() + 1, Math.min(size + (it + 1) * cols + less1, realSize));
            }

            result.addAll(item.entrySet());
        });


        return result;
    }

    /**
     * 平方根算法<br>
     * 推荐使用 {@link Math#sqrt(double)}
     *
     * @param x 值
     * @return 平方根
     */
    public static long sqrt(long x) {
        long y = 0;
        long b = (~Long.MAX_VALUE) >>> 1;
        while (b > 0) {
            if (x >= y + b) {
                x -= y + b;
                y >>= 1;
                y += b;
            } else {
                y >>= 1;
            }
            b >>= 2;
        }
        return y;
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static double sub(float v1, float v2) {
        return sub(Float.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static double sub(float v1, double v2) {
        return sub(Float.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static double sub(double v1, float v2) {
        return sub(Double.toString(v1), Float.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static double sub(double v1, double v2) {
        return sub(Double.toString(v1), Double.toString(v2)).doubleValue();
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static double sub(Double v1, Double v2) {
        return sub((Number) v1, (Number) v2).doubleValue();
    }

    /**
     * 提供精确的减法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static BigDecimal sub(Number v1, Number v2) {
        return sub(new Number[]{v1, v2});
    }

    /**
     * 提供精确的减法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被减值
     * @return 差
     * @since 4.0.0
     */
    public static BigDecimal sub(Number... values) {
        if (ArrayUtils.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        Number value = values[0];
        BigDecimal result = null == value ? BigDecimal.ZERO : new BigDecimal(value.toString());
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (null != value) {
                result = result.subtract(new BigDecimal(value.toString()));
            }
        }
        return result;
    }


    /**
     * 提供精确的减法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被减值
     * @return 差
     * @since 4.0.0
     */
    public static BigDecimal sub(String... values) {
        if (ArrayUtils.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        String value = values[0];
        BigDecimal result = null == value ? BigDecimal.ZERO : new BigDecimal(value);
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (null != value) {
                result = result.subtract(new BigDecimal(value));
            }
        }
        return result;
    }

    /**
     * 提供精确的减法运算<br>
     * 如果传入多个值为null或者空，则返回0
     *
     * @param values 多个被减值
     * @return 差
     * @since 4.0.0
     */
    public static BigDecimal sub(BigDecimal... values) {
        if (ArrayUtils.isEmpty(values)) {
            return BigDecimal.ZERO;
        }

        BigDecimal value = values[0];
        BigDecimal result = null == value ? BigDecimal.ZERO : value;
        for (int i = 1; i < values.length; i++) {
            value = values[i];
            if (null != value) {
                result = result.subtract(value);
            }
        }
        return result;
    }

    /**
     * 减法。计算结果四舍五入。
     *
     * @param minuend   被减数
     * @param reduction 减数
     * @param scale     计算结果保留位数。(注意包括整数部分)
     * @return 计算结果
     */
    public static double subtract(double minuend, double reduction, int scale) {
        BigDecimal minuendBd = BigDecimal.valueOf(minuend);
        BigDecimal reductionBd = BigDecimal.valueOf(reduction);
        MathContext mathContext = new MathContext(scale, RoundingMode.HALF_UP);
        return minuendBd.subtract(reductionBd, mathContext).doubleValue();
    }

    /**
     * 减法。
     *
     * @param minuend   被减数
     * @param reduction 减数
     * @return 计算结果
     */
    public static double subtract(double minuend, double reduction) {
        BigDecimal minuendBd = BigDecimal.valueOf(minuend);
        BigDecimal reductionBd = BigDecimal.valueOf(reduction);
        return minuendBd.subtract(reductionBd).doubleValue();
    }

    /**
     * 数字转{@link BigDecimal}
     *
     * @param number 数字
     * @return {@link BigDecimal}
     * @since 4.0.9
     */
    public static BigDecimal toBigDecimal(Number number) {
        if (null == number) {
            return BigDecimal.ZERO;
        }

        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        } else if (number instanceof Long) {
            return new BigDecimal((Long) number);
        } else if (number instanceof Integer) {
            return new BigDecimal((Integer) number);
        } else if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        }

        return toBigDecimal(number.toString());
    }

    /**
     * 数字转{@link BigDecimal}
     *
     * @param number 数字
     * @return {@link BigDecimal}
     * @since 4.0.9
     */
    public static BigDecimal toBigDecimal(String number) {
        return (null == number) ? BigDecimal.ZERO : new BigDecimal(number);
    }

    /**
     * 数字转{@link BigInteger}
     *
     * @param number 数字
     * @return {@link BigInteger}
     * @since 5.4.5
     */
    public static BigInteger toBigInteger(Number number) {
        if (null == number) {
            return BigInteger.ZERO;
        }

        if (number instanceof BigInteger) {
            return (BigInteger) number;
        } else if (number instanceof Long) {
            return BigInteger.valueOf((Long) number);
        }

        return toBigInteger(number.longValue());
    }

    /**
     * 数字转{@link BigInteger}
     *
     * @param number 数字
     * @return {@link BigInteger}
     * @since 5.4.5
     */
    public static BigInteger toBigInteger(String number) {
        return (null == number) ? BigInteger.ZERO : new BigInteger(number);
    }

    /**
     * <p>Convert a <code>String</code> to a <code>byte</code>, returning
     * <code>zero</code> if the conversion fails.</p>
     *
     * <p>If the string is <code>null</code>, <code>zero</code> is returned.</p>
     *
     * <pre>
     *   NumberHelper.toByte(null) = 0
     *   NumberHelper.toByte("")   = 0
     *   NumberHelper.toByte("1")  = 1
     * </pre>
     *
     * @param str 原始数据
     * @return the byte represented by the string, or <code>zero</code> if
     * conversion fails
     * @since 2.5
     */
    public static byte toByte(final String str) {
        return toByte(str, (byte) 0);
    }

    /**
     * <p>Convert a <code>String</code> to a <code>byte</code>, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string is <code>null</code>, the default value is returned.</p>
     *
     * <pre>
     *   NumberHelper.toByte(null, 1) = 1
     *   NumberHelper.toByte("", 1)   = 1
     *   NumberHelper.toByte("1", 0)  = 1
     * </pre>
     *
     * @param str          原始数据
     * @param defaultValue 默认值
     * @return the byte represented by the string, or the default if conversion fails
     * @since 2.5
     */
    public static byte toByte(final String str, final byte defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Byte.parseByte(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * <p>Convert a <code>String</code> to a <code>double</code>, returning
     * <code>0.0d</code> if the conversion fails.</p>
     *
     * <p>If the string <code>str</code> is <code>null</code>,
     * <code>0.0d</code> is returned.</p>
     *
     * <pre>
     *   NumberHelper.toDouble(null)   = 0.0d
     *   NumberHelper.toDouble("")     = 0.0d
     *   NumberHelper.toDouble("1.5")  = 1.5d
     * </pre>
     *
     * @param str the string to convert, may be <code>null</code>
     * @return the double represented by the string, or <code>0.0d</code>
     * if conversion fails
     * @since 2.1
     */
    public static double toDouble(final String str) {
        return toDouble(str, 0.0d);
    }

    /**
     * Number值转换为double<br>
     * float强制转换存在精度问题，此方法避免精度丢失
     *
     * @param value 被转换的float值
     * @return double值
     * @since 5.7.8
     */
    public static double toDouble(Number value) {
        if (value instanceof Float) {
            return Double.parseDouble(value.toString());
        } else {
            return value.doubleValue();
        }
    }


    /**
     * <p>Convert a <code>String</code> to a <code>double</code>, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string <code>str</code> is <code>null</code>, the default
     * value is returned.</p>
     *
     * <pre>
     *   NumberHelper.toDouble(null, 1.1d)   = 1.1d
     *   NumberHelper.toDouble("", 1.1d)     = 1.1d
     *   NumberHelper.toDouble("1.5", 0.0d)  = 1.5d
     * </pre>
     *
     * @param str          the string to convert, may be <code>null</code>
     * @param defaultValue the default value
     * @return the double represented by the string, or defaultValue
     * if conversion fails
     * @since 2.1
     */
    public static double toDouble(final String str, final double defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * <p>Convert a <code>BigDecimal</code> to a <code>double</code>.</p>
     *
     * <p>If the <code>BigDecimal</code> <code>value</code> is
     * <code>null</code>, then the specified default value is returned.</p>
     *
     * <pre>
     *   NumberHelper.toDouble(null)                     = 0.0d
     *   NumberHelper.toDouble(BigDecimal.valueOf(8.5d)) = 8.5d
     * </pre>
     *
     * @param value the <code>BigDecimal</code> to convert, may be <code>null</code>.
     * @return the double represented by the <code>BigDecimal</code> or
     * <code>0.0d</code> if the <code>BigDecimal</code> is <code>null</code>.
     * @since 3.8
     */
    public static double toDouble(final BigDecimal value) {
        return toDouble(value, 0.0d);
    }

    /**
     * <pre>
     *   NumberHelper.toDouble(null, 1.1d)                     = 1.1d
     *   NumberHelper.toDouble(BigDecimal.valueOf(8.5d), 1.1d) = 8.5d
     * </pre>
     *
     * @param value        the <code>BigDecimal</code> to convert, may be <code>null</code>.
     * @param defaultValue the default value
     * @return the double represented by the <code>BigDecimal</code> or the
     * defaultValue if the <code>BigDecimal</code> is <code>null</code>.
     * @since 3.8
     */
    public static double toDouble(final BigDecimal value, final double defaultValue) {
        return value == null ? defaultValue : value.doubleValue();
    }

    /**
     * <p>Convert a <code>String</code> to a <code>double</code>, returning
     * <code>0.0d</code> if the conversion fails.</p>
     *
     * <p>If the string <code>str</code> is <code>null</code>,
     * <code>0.0d</code> is returned.</p>
     *
     * <pre>
     *   NumberHelper.toDoubleValue(null)   = 0.0d
     *   NumberHelper.toDoubleValue("")     = 0.0d
     *   NumberHelper.toDoubleValue("1.5")  = 1.5d
     * </pre>
     *
     * @param str the string to convert, may be <code>null</code>
     * @return the double represented by the string, or <code>0.0d</code>
     * if conversion fails
     * @since 2.1
     */
    public static double toDoubleValue(final String str) {
        return toDoubleValue(str, 0.0d);
    }

    /**
     * <p>Convert a <code>String</code> to a <code>double</code>, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string <code>str</code> is <code>null</code>, the default
     * value is returned.</p>
     *
     * <pre>
     *   NumberHelper.toDoubleValue(null, 1.1d)   = 1.1d
     *   NumberHelper.toDoubleValue("", 1.1d)     = 1.1d
     *   NumberHelper.toDoubleValue("1.5", 0.0d)  = 1.5d
     * </pre>
     *
     * @param str          the string to convert, may be <code>null</code>
     * @param defaultValue the default value
     * @return the double represented by the string, or defaultValue
     * if conversion fails
     * @since 2.1
     */
    public static double toDoubleValue(final String str, final double defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * <p>Convert a <code>String</code> to a <code>float</code>, returning
     * <code>0.0f</code> if the conversion fails.</p>
     *
     * <p>If the string <code>str</code> is <code>null</code>,
     * <code>0.0f</code> is returned.</p>
     *
     * <pre>
     *   NumberHelper.toFloat(null)   = 0.0f
     *   NumberHelper.toFloat("")     = 0.0f
     *   NumberHelper.toFloat("1.5")  = 1.5f
     * </pre>
     *
     * @param str the string to convert, may be <code>null</code>
     * @return the float represented by the string, or <code>0.0f</code>
     * if conversion fails
     * @since 2.1
     */
    public static Float toFloat(final String str) {
        return toFloat(str, null);
    }

    /**
     * <p>Convert a <code>String</code> to a <code>float</code>, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string <code>str</code> is <code>null</code>, the default
     * value is returned.</p>
     *
     * <pre>
     *   NumberHelper.toFloat(null, 1.1f)   = 1.0f
     *   NumberHelper.toFloat("", 1.1f)     = 1.1f
     *   NumberHelper.toFloat("1.5", 0.0f)  = 1.5f
     * </pre>
     *
     * @param str          the string to convert, may be <code>null</code>
     * @param defaultValue the default value
     * @return the float represented by the string, or defaultValue
     * if conversion fails
     * @since 2.1
     */
    public static Float toFloat(final String str, final Float defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * <p>Convert a <code>String</code> to a <code>float</code>, returning
     * <code>0.0f</code> if the conversion fails.</p>
     *
     * <p>If the string <code>str</code> is <code>null</code>,
     * <code>0.0f</code> is returned.</p>
     *
     * <pre>
     *   NumberHelper.toFloatValue(null)   = 0.0f
     *   NumberHelper.toFloatValue("")     = 0.0f
     *   NumberHelper.toFloatValue("1.5")  = 1.5f
     * </pre>
     *
     * @param str the string to convert, may be <code>null</code>
     * @return the float represented by the string, or <code>0.0f</code>
     * if conversion fails
     * @since 2.1
     */
    public static float toFloatValue(final String str) {
        return toFloatValue(str, 0.0f);
    }

    /**
     * <p>Convert a <code>String</code> to a <code>float</code>, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string <code>str</code> is <code>null</code>, the default
     * value is returned.</p>
     *
     * <pre>
     *   NumberHelper.toFloatValue(null, 1.1f)   = 1.1f
     *   NumberHelper.toFloatValue("", 1.1f)     = 1.1f
     *   NumberHelper.toFloatValue("1.5", 0.0f)  = 1.5f
     * </pre>
     *
     * @param str          the string to convert, may be <code>null</code>
     * @param defaultValue the default value
     * @return the float represented by the string, or defaultValue
     * if conversion fails
     * @since 2.1
     */
    public static float toFloatValue(final String str, final float defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * <pre>
     *   NumberHelper.toInt(null) = 0
     *   NumberHelper.toInt("")   = 0
     *   NumberHelper.toInt("1")  = 1
     * </pre>
     *
     * @param str 参数
     * @return int
     */
    public static int toInt(final String str) {
        return toInt(str, 0);
    }

    /**
     * <pre>
     *   NumberHelper.toInt(null) = 0
     *   NumberHelper.toInt(1L)  = 1
     * </pre>
     *
     * @param longValue 整型
     * @return int 数据
     */
    public static Integer toInt(final Long longValue) {
        return null == longValue ? null : longValue.intValue();
    }

    /**
     * <pre>
     *   NumberHelper.toInt(null, 1) = 1
     *   NumberHelper.toInt("", 1)   = 1
     *   NumberHelper.toInt("1", 0)  = 1
     * </pre>
     *
     * @param source       原始数据
     * @param defaultValue 默认值
     */
    public static int toInt(final String source, final int defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(source);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * <pre>
     *   NumberHelper.toIntValue(null) = 0
     *   NumberHelper.toIntValue(1L)  = 1
     * </pre>
     *
     * @param longValue 整型
     * @return int 数据
     */
    public static int toIntValue(final Long longValue) {
        return null == longValue ? 0 : longValue.intValue();
    }

    /**
     * <pre>
     *   NumberHelper.toInt(null) = 0
     *   NumberHelper.toInt("")   = 0
     *   NumberHelper.toInt("1")  = 1
     * </pre>
     *
     * @param str 参数
     * @return int
     */
    public static int toInteger(final String str) {
        return toInteger(str, null);
    }

    /**
     * <pre>
     *   NumberHelper.Integer(null, 1) = 1
     *   NumberHelper.Integer("", 1)   = 1
     *   NumberHelper.Integer("1", 0)  = 1
     * </pre>
     *
     * @param source       原始数据
     * @param defaultValue 默认值
     */
    public static Integer toInteger(final String source, final Integer defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(source);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * <pre>
     *   NumberHelper.toLong(null) = null
     *   NumberHelper.toLong("")   = null
     *   NumberHelper.toLong("1")  = 1L
     * </pre>
     *
     * @param str 原始数据
     * @since 2.1
     */
    public static Long toLong(final String str) {
        return toLong(str, 0L);
    }

    /**
     * <pre>
     *   NumberHelper.toLong(null, 1L) = 1L
     *   NumberHelper.toLong("", 1L)   = 1L
     *   NumberHelper.toLong("1", 0L)  = 1L
     * </pre>
     *
     * @param str          原始数据
     * @param defaultValue 默认值
     * @return the long represented by the string, or the default if conversion fails
     * @since 2.1
     */
    public static Long toLong(final String str, final Long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * <pre>
     *   NumberHelper.toLongValue(null) = 0L
     *   NumberHelper.toLongValue("")   = 0L
     *   NumberHelper.toLongValue("1")  = 1L
     * </pre>
     *
     * @param str 原始数据
     * @since 2.1
     */
    public static long toLongValue(final String str) {
        return toLongValue(str, 0L);
    }

    /**
     * <pre>
     *   NumberHelper.toLongValue(null, 1L) = 1L
     *   NumberHelper.toLongValue("", 1L)   = 1L
     *   NumberHelper.toLongValue("1", 0L)  = 1L
     * </pre>
     *
     * @param str          原始数据
     * @param defaultValue 默认值
     * @return the long represented by the string, or the default if conversion fails
     * @since 2.1
     */
    public static long toLongValue(final String str, final long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * @param value 原始数据
     * @since 3.8
     */
    public static BigDecimal toScaledBigDecimal(final BigDecimal value) {
        return toScaledBigDecimal(value, 2, RoundingMode.HALF_EVEN);
    }

    /**
     * @param value        原始数据
     * @param scale        数据缩放
     * @param roundingMode 模式
     * @since 3.8
     */
    public static BigDecimal toScaledBigDecimal(final BigDecimal value, final int scale, final RoundingMode roundingMode) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(scale, (roundingMode == null) ? RoundingMode.HALF_EVEN : roundingMode);
    }

    /**
     * toScaledBigDecimal
     *
     * @param value 原始数据
     * @return BigDecimal
     * @since 3.8
     */
    public static BigDecimal toScaledBigDecimal(final Float value) {
        return toScaledBigDecimal(value, 2, RoundingMode.HALF_EVEN);
    }

    /**
     * toScaledBigDecimal
     *
     * @param value        原始数据
     * @param scale        数据缩放
     * @param roundingMode roundingMode
     * @return BigDecimal
     * @since 3.8
     */
    public static BigDecimal toScaledBigDecimal(final Float value, final int scale, final RoundingMode roundingMode) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return toScaledBigDecimal(
                BigDecimal.valueOf(value),
                scale,
                roundingMode
        );
    }

    /**
     * toScaledBigDecimal
     *
     * @param value 原始数据
     * @return BigDecimal
     * @since 3.8
     */
    public static BigDecimal toScaledBigDecimal(final Double value) {
        return toScaledBigDecimal(value, 2, RoundingMode.HALF_EVEN);
    }

    /**
     * toScaledBigDecimal
     *
     * @param value        原始数据
     * @param scale        数据缩放
     * @param roundingMode roundingMode
     * @return BigDecimal
     * @since 3.8
     */
    public static BigDecimal toScaledBigDecimal(final Double value, final int scale, final RoundingMode roundingMode) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return toScaledBigDecimal(
                BigDecimal.valueOf(value),
                scale,
                roundingMode
        );
    }

    /**
     * <p>Convert a <code>String</code> to a <code>short</code>, returning
     * <code>zero</code> if the conversion fails.</p>
     *
     * <p>If the string is <code>null</code>, <code>zero</code> is returned.</p>
     *
     * <pre>
     *   NumberHelper.toShort(null) = 0
     *   NumberHelper.toShort("")   = 0
     *   NumberHelper.toShort("1")  = 1
     * </pre>
     *
     * @param str 原始数据
     * @return the short represented by the string, or <code>zero</code> if
     * conversion fails
     * @since 2.5
     */
    public static short toShort(final String str) {
        return toShort(str, (short) 0);
    }

    /**
     * <p>Convert a <code>String</code> to an <code>short</code>, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string is <code>null</code>, the default value is returned.</p>
     *
     * <pre>
     *   NumberHelper.toShort(null, 1) = 1
     *   NumberHelper.toShort("", 1)   = 1
     *   NumberHelper.toShort("1", 0)  = 1
     * </pre>
     *
     * @param str          原始数据
     * @param defaultValue 默认值
     * @return the short represented by the string, or the default if conversion fails
     * @since 2.5
     */
    public static short toShort(final String str, final short defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Short.parseShort(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * 解析bigint
     *
     * @param value 参数
     * @return BigInteger
     */
    private static BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;

        if (value.startsWith(SYMBOL_MINS)) {
            negative = true;
            index++;
        }

        if (value.startsWith(HEX_16, index) || value.startsWith(HEX_16_UPPER, index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith(SYMBOL_HASH, index)) {
            index++;
            radix = 16;
        } else if (value.startsWith(ZERO, index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }

        BigInteger result = new BigInteger(value.substring(index), radix);
        return (negative ? result.negate() : result);
    }

    /**
     * 是否是Hex字符串
     *
     * @param value 值
     * @return 是否是Hex字符串
     */
    private static boolean isHexNumber(final String value) {
        int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith(HEX_16, index) || value.startsWith(HEX_16_UPPER, index) || value.startsWith(SYMBOL_HASH, index));
    }


    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回小于0的数，x&gt;y返回大于0的数
     * @see Character#compare(char, char)
     * @since 3.0.1
     */
    public static int compare(char x, char y) {
        return Character.compare(x, y);
    }

    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回小于0的数，x&gt;y返回大于0的数
     * @see Double#compare(double, double)
     * @since 3.0.1
     */
    public static int compare(double x, double y) {
        return Double.compare(x, y);
    }

    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回小于0的数，x&gt;y返回大于0的数
     * @see Integer#compare(int, int)
     * @since 3.0.1
     */
    public static int compare(int x, int y) {
        return Integer.compare(x, y);
    }

    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回小于0的数，x&gt;y返回大于0的数
     * @see Long#compare(long, long)
     * @since 3.0.1
     */
    public static int compare(long x, long y) {
        return Long.compare(x, y);
    }

    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回小于0的数，x&gt;y返回大于0的数
     * @see Short#compare(short, short)
     * @since 3.0.1
     */
    public static int compare(short x, short y) {
        return Short.compare(x, y);
    }

    /**
     * 比较两个值的大小
     *
     * @param x 第一个值
     * @param y 第二个值
     * @return x==y返回0，x&lt;y返回-1，x&gt;y返回1
     * @see Byte#compare(byte, byte)
     * @since 3.0.1
     */
    public static int compare(byte x, byte y) {
        return Byte.compare(x, y);
    }

    /**
     * 比较大小，参数1 &gt; 参数2 返回true
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否大于
     * @since 3.0.9
     */
    public static boolean isGreater(BigDecimal bigNum1, BigDecimal bigNum2) {
        return bigNum1.compareTo(bigNum2) > 0;
    }

    /**
     * 比较大小，参数1 &gt;= 参数2 返回true
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否大于等于
     * @since 3, 0.9
     */
    public static boolean isGreaterOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
        return bigNum1.compareTo(bigNum2) >= 0;
    }

    /**
     * 比较大小，参数1 &lt; 参数2 返回true
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否小于
     * @since 3, 0.9
     */
    public static boolean isLess(BigDecimal bigNum1, BigDecimal bigNum2) {
        return bigNum1.compareTo(bigNum2) < 0;
    }

    /**
     * 比较大小，参数1&lt;=参数2 返回true
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否小于等于
     * @since 3, 0.9
     */
    public static boolean isLessOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
        return bigNum1.compareTo(bigNum2) <= 0;
    }

    /**
     * 比较大小，值相等 返回true<br>
     * 此方法通过调用{@link Double#doubleToLongBits(double)}方法来判断是否相等<br>
     * 此方法判断值相等时忽略精度的，即0.00 == 0
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 是否相等
     * @since 5.4.2
     */
    public static boolean equals(double num1, double num2) {
        return Double.doubleToLongBits(num1) == Double.doubleToLongBits(num2);
    }

    /**
     * 比较大小，值相等 返回true<br>
     * 此方法通过调用{@link Float#floatToIntBits(float)}方法来判断是否相等<br>
     * 此方法判断值相等时忽略精度的，即0.00 == 0
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 是否相等
     * @since 5.4.5
     */
    public static boolean equals(float num1, float num2) {
        return Float.floatToIntBits(num1) == Float.floatToIntBits(num2);
    }

    /**
     * 比较大小，值相等 返回true<br>
     * 此方法通过调用{@link BigDecimal#compareTo(BigDecimal)}方法来判断是否相等<br>
     * 此方法判断值相等时忽略精度的，即0.00 == 0
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否相等
     */
    public static boolean equals(BigDecimal bigNum1, BigDecimal bigNum2) {
        if (bigNum1.equals(bigNum2)) {
            return true;
        }
        if (bigNum1 == null || bigNum2 == null) {
            return false;
        }
        return 0 == bigNum1.compareTo(bigNum2);
    }

    /**
     * zero
     *
     * @param value        值
     * @param defaultValue 默认值
     * @return 结果
     */
    public static int isZero(int value, int defaultValue) {
        return value == 0 ? defaultValue : value;
    }

    /**
     * zero
     *
     * @param value        值
     * @param defaultValue 默认值
     * @return 结果
     */
    public static double isZero(double value, double defaultValue) {
        return value == 0.0d ? defaultValue : value;
    }

    /**
     * excel单元格
     *
     * @param value 数据
     * @return 单元格
     */
    public static String toExcelCell(int value) {
        value = value - 65;
        int size = value / 26;
        int less = value % 26;
        System.out.println();

        return StringUtils.repeat("A", size) + (char) (less + 65);
    }


    /**
     * 中文数字转阿拉伯数字
     * 一万两千三百五十四 --> 12354
     *
     * @param chinese 阿拉伯数字
     * @return 中文数字
     */
    public static String getNumberFromChinese(String chinese) {
        String result = "0";
        List<String> lists = new ArrayList<>();
        int lastLevelIndex = 0;
        for (int i = NumberValue.HIGH_LEVEL.size() - 1; i >= 0; i--) {
            int levelIndex = chinese.indexOf(NumberValue.HIGH_LEVEL.get(i));
            if (levelIndex > 0) {
                lists.add(chinese.substring(0, levelIndex));
                chinese = chinese.substring(levelIndex + 1);
            } else if (levelIndex == -1) {
                lists.add(NumberValue.NUMBER.get(0));
            } else if (levelIndex == 0) {
                while (levelIndex > 1) {
                    levelIndex--;
                    lists.add(NumberValue.NUMBER.get(0));
                }
                lists.add(chinese);
            }
        }
        for (int i = 0; i < lists.size(); i++) {
            Integer highLevelIndex = lists.size() - i - 1;
            String single = lists.get(i);
            String nextResult = getNumberFromChinese(single);
            if (SYMBOL_EOF.equals(nextResult)) {
                throw new NumberFormatException();
            }
            Long next = (long) (Integer.parseInt(result) * (int) (Math.pow(10, 4)) + Integer.parseInt(nextResult));
            result = next.toString();
        }
        result = result.replaceFirst("^(0+)", "");
        return result;
    }

    /**
     * 通过中文数字获取4位数阿拉伯数字
     * 万以内的数据转换
     *
     * @param single 中文数字
     * @return 4位数阿拉伯数字
     */
    private static String getNumberFromChinese(String single) {
        String result = "0";
        int highIndex = 1;
        for (int i = 0; i < single.length(); i++) {
            String str = String.valueOf(single.charAt(i));
            int unit = NumberValue.LEVEL.indexOf(str);
            int number = NumberValue.NUMBER.indexOf(str);
            if (-1 == number) {
                number = NumberValue.BIG_NUMBER.indexOf(str);
            }
            if (unit == -1) {
                int next = 0;
                if (i < single.length() - 1) {
                    next = NumberValue.LEVEL.indexOf(String.valueOf(single.charAt(i + 1)));
                }
                result = String.valueOf(Integer.parseInt(result) + number * (int) (Math.pow(10, next)));
            }
        }
        result = "" + Integer.parseInt(result) * (int) (Math.pow(10, 0));
        return result;
    }

    /**
     * 阿拉伯数字转中文数字
     * 12354 --> 一万两千三百五十四
     *
     * @param alabo 阿拉伯数字
     * @return 中文数字
     */
    public static String getNumberFromAlamo(String alabo) {
        StringBuilder result = new StringBuilder();
        List<String> list = new ArrayList<>();
        for (int length = alabo.length() - 1; length >= 0; length--) {
            list.add(String.valueOf(alabo.charAt(length)));
        }
        List<List<String>> lists = CollectionUtils.averageAssign(list, 4);
        Collections.reverse(lists);
        if (CollectionUtils.isNotEmpty(lists)) {
            for (int index = 0; index < lists.size(); index++) {
                List<String> singleNumList = lists.get(index);
                Collections.reverse(singleNumList);
                Boolean zeroflag = false;
                StringBuilder chinese = new StringBuilder();
                for (int j = 0; j < singleNumList.size(); j++) {
                    Integer number = Integer.valueOf(singleNumList.get(j));
                    if (number == 0 && !zeroflag && afterNotAllZero(singleNumList, j)) {
                        chinese.append(NumberValue.NUMBER.get(number));
                        zeroflag = true;
                    } else if (number != 0) {
                        chinese.append(NumberValue.NUMBER.get(number)).append(NumberValue.LEVEL.get(singleNumList.size() - j - 1));
                    }
                }
                if (index == lists.size() && chinese.substring(0, 1).equals(NumberValue.NUMBER.get(0))) {
                    chinese = new StringBuilder(chinese.substring(1));
                }
                if (chinese.length() > 0 && !NumberValue.HIGH_LEVEL.contains(chinese.substring(chinese.length() - 1))) {
                    result.append(chinese).append(NumberValue.HIGH_LEVEL.get(lists.size() - 1 - index));
                }
            }
        }
        return result.toString();
    }

    /**
     * 判断singleNumList在j位置之后是否全是0
     *
     * @param singleNumList 字符集
     * @param offset        位置
     * @return 是否全不为0
     */
    private static boolean afterNotAllZero(List<String> singleNumList, int offset) {
        for (int i = offset + 1; i < singleNumList.size(); i++) {
            if (!"0".equals(singleNumList.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 方差
     *
     * @param data 数据
     * @return 方差
     */
    public static double getVariance(double[] data) {
        int m = data.length;
        double sum = 0;
        for (int i = 0; i < m; i++) {
            sum += data[i];
        }
        double dAve = sum / m;
        double dVar = 0;
        for (int i = 0; i < m; i++) {
            dVar += (data[i] - dAve) * (data[i] - dAve);
        }
        return dVar / m;
    }

    /**
     * 平均数
     *
     * @return 平均数
     */
    public static double getAverage(double[] data) {
        BigDecimal bigDecimal = BigDecimal.ZERO;
        for (double datum : data) {
            bigDecimal = bigDecimal.add(BigDecimal.valueOf(datum));
        }

        return bigDecimal.divide(BigDecimal.valueOf(data.length), 15, 1).doubleValue();
    }

    /**
     * sigma
     *
     * @return sigma
     */
    public static double getStandardDeviation(double[] data) {
        int m = data.length;
        double sum = 0;
        for (int i = 0; i < m; i++) {
            sum += data[i];
        }
        double dAve = sum / m;
        double dVar = 0;
        for (int i = 0; i < m; i++) {
            dVar += (data[i] - dAve) * (data[i] - dAve);
        }
        return Math.sqrt(dVar / m);
    }

    /**
     * 回归
     *
     * @param x            x轴
     * @param mean         平均数
     * @param variance     方差
     * @param stdDeviation 标准差
     * @return 回归
     */
    public static double getY(double x, double mean, double variance, double stdDeviation) {
        return Math.pow(Math.exp(-(((x - mean) * (x - mean)) / ((2 * variance)))), 1 / (stdDeviation * Math.sqrt(2 * Math.PI)));
    }

}
