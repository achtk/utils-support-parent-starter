package com.chua.common.support.utils;

/**
 * Preconditions
 *
 * @author CH
 */
public final class Preconditions {
    /**
     * 参数是否合法
     *
     * @param expression 表达式
     */
    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 参数是否合法
     *
     * @param expression   表达式
     * @param errorMessage 提示信息
     */

    public static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    /**
     * 参数是否合法
     *
     * @param expression       表达式
     * @param errorMessage     提示信息
     * @param errorMessageArgs 参数
     */

    public static void checkArgument(
            boolean expression,
            String errorMessage,
            Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(StringUtils.format(errorMessage, errorMessageArgs));
        }
    }

    /**
     * 参数是否合法
     *
     * @param expression 表达式
     */
    public static void checkRoundingUnnecessary(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 参数是否合法
     *
     * @param expression   表达式
     * @param errorMessage 提示信息
     */

    public static void checkRoundingUnnecessary(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    /**
     * 检查值是否合法
     *
     * @param value 值
     * @param name  字段名称
     * @return 返回合法数据
     */
    public static long checkNonNegative(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
        return value;
    }

    /**
     * 检查值是否合法
     *
     * @param value 值
     * @param name  字段名称
     * @return 返回合法数据
     */
    public static long checkNonNegative(String name, long value) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
        return value;
    }

    /**
     * 检查对象是否为空
     *
     * @param reference 对象
     * @param <T>       类型
     * @return 检查对象是否为空
     */
    public static <T> T checkNotNull(T reference) {
        if (null == reference) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * 检查对象是否为空
     *
     * @param reference 对象
     * @param <T>       类型
     * @param message   提示信息
     * @return 检查对象是否为空
     */
    public static void checkNotNull(String reference, String message) {
        if (StringUtils.isEmpty(reference)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * uncheck
     *
     * @param t   对象
     * @param <T> 类型
     * @return 对象
     */
    public static <T extends Object> T uncheckedCastNullableTToT(T t) {
        return t;
    }

    /**
     * 参数是否合法
     *
     * @param role 提示消息
     * @param x    值
     * @return 参数是否合法
     */

    public static int checkPositive(String role, int x) {
        if (x <= 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
        return x;
    }

    /**
     * 参数是否合法
     *
     * @param role 提示消息
     * @param x    值
     * @return 参数是否合法
     */

    public static long checkPositive(String role, long x) {
        if (x <= 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
        return x;
    }

    /**
     * 检查参数合法性
     *
     * @param condition  表达式
     * @param methodName 方法
     * @param a          参数1
     * @param b          参数2
     */
    public static void checkNoOverflow(boolean condition, String methodName, int a, int b) {
        if (!condition) {
            throw new ArithmeticException("overflow: " + methodName + "(" + a + ", " + b + ")");
        }
    }

    /**
     * 检查参数合法性
     *
     * @param condition  表达式
     * @param methodName 方法
     * @param a          参数1
     * @param b          参数2
     */
    public static void checkNoOverflow(boolean condition, String methodName, long a, long b) {
        if (!condition) {
            throw new ArithmeticException("overflow: " + methodName + "(" + a + ", " + b + ")");
        }
    }


}
