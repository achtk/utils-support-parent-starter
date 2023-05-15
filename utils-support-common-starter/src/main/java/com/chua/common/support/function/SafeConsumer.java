package com.chua.common.support.function;

import java.util.function.Consumer;

/**
 * 选项
 *
 * @author CH
 * @version 1.0.0
 */
public interface SafeConsumer<T> extends Consumer<T> {
    /**
     * 获取数据
     *
     * @param t 参数
     */
    @Override
    default void accept(T t) {
        try {
            safeAccept(t);
        } catch (Throwable ignored) {
        }
    }

    /**
     * 获取结果
     *
     * @param t 参数
     * @throws Throwable Throwable
     */
    void safeAccept(T t) throws Throwable;
}

