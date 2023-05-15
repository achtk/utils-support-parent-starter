package com.chua.common.support.function;


import com.chua.common.support.utils.ClassUtils;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 可选
 *
 * @author CH
 */
public class SafeOptional<T, R> {

    private static final SafeOptional<?, ?> EMPTY = new SafeOptional<>(false, null);
    private final boolean isNone;
    private final R apply;

    public SafeOptional(boolean isNone, R apply) {
        this.isNone = isNone;
        this.apply = apply;
    }

    /**
     * 获取值
     *
     * @return 结果
     */
    public R get() {
        return apply;
    }

    /**
     * if-else
     *
     * @param value    值
     * @param function 回调
     * @return 结果
     */
    public SafeOptional<T, R> elseCapable(T value, Function<T, R> function) {
        if (isNone) {
            return new SafeOptional<>(true, apply);
        }
        return ifCapable(value, function);
    }

    /**
     * if-else
     *
     * @param value    值
     * @param function 回调
     * @return 结果
     */
    public SafeOptional<T, R> elseCapable(boolean condition, T value, Function<T, R> function) {
        if (isNone) {
            return new SafeOptional<>(true, apply);
        }
        return ifCapable(value, function);
    }

    /**
     * if-else
     *
     * @param function 回调
     * @return 结果
     */
    public SafeOptional<T, R> elseCapable(Supplier<R> function) {
        if (isNone) {
            return new SafeOptional<>(true, apply);
        }
        return new SafeOptional<>(true, function.get());
    }

    /**
     * 是否为空
     *
     * @param value 是否为空
     * @return this
     */
    public static <T, R> SafeOptional<T, R> ifCapable(T value, Function<T, R> function) {
        if (null == value || ClassUtils.isVoid(value)) {
            return (SafeOptional<T, R>) SafeOptional.EMPTY;
        }

        return new SafeOptional<T, R>(true, function.apply(value));
    }

    /**
     * 是否为空
     *
     * @param value 是否为空
     * @return this
     */
    public static <R> SafeOptional<Boolean, R> ifCapable(boolean value, Function<Boolean, R> function) {
        if (!value) {
            return (SafeOptional<Boolean, R>) SafeOptional.EMPTY;
        }

        return new SafeOptional<Boolean, R>(value, function.apply(value));
    }
}
