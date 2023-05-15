package com.chua.common.support.function;

import java.util.function.Supplier;

/**
 * 选项
 *
 * @author CH
 * @version 1.0.0
 */
public interface SafeSupplier<T> extends Supplier<T> {
    /**
     * 获取数据
     *
     * @return 数据
     */
    @Override
    default T get() {
        try {
            return safeGet();
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * 获取结果
     *
     * @return 结果
     * @throws Throwable Throwable
     */
    T safeGet() throws Throwable;
}

