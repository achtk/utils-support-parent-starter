package com.chua.common.support.function;

import java.util.function.BiConsumer;

/**
 * 选项
 *
 * @author CH
 * @version 1.0.0
 */
public interface SafeBiConsumer<T, U> extends BiConsumer<T, U> {
    /**
     * 获取数据
     *
     * @param u u
     * @param t 参数
     */
    @Override
    default void accept(T t, U u) {
        try {
            safeAccept(t, u);
        } catch (Throwable ignored) {
        }
    }

    /**
     * 获取结果
     *
     * @param u u
     * @param t 参数
     * @throws Throwable Throwable
     */
    void safeAccept(T t, U u) throws Throwable;
}

