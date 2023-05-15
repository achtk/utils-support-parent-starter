package com.chua.common.support.function;

import java.util.function.Function;

/**
 * 选项
 *
 * @author CH
 * @version 1.0.0
 */
public interface SafeFunction<T, R> extends Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    @Override
    default R apply(T t) {
        try {
            return safeApply(t);
        } catch (Throwable throwable) {
            return null;
        }
    }

    /**
     * 获取结果
     *
     * @param t 参数
     * @return 结果
     * @throws Throwable Throwable
     */
    R safeApply(T t) throws Throwable;
}

