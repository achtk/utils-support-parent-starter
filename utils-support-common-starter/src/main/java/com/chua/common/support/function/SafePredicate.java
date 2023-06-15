package com.chua.common.support.function;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 选项
 *
 * @author CH
 * @version 1.0.0
 */
public interface SafePredicate<T> extends Predicate<T> {
    /**
     * 获取数据
     *
     * @param t 参数
     */
    @Override
    default boolean test(T t) {
        try {
            return safeTest(t);
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * 获取结果
     *
     * @param t 参数
     * @throws Throwable Throwable
     */
    boolean safeTest(T t) throws Throwable;
}

