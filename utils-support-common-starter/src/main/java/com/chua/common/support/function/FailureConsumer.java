package com.chua.common.support.function;

import java.util.function.Consumer;

/**
 * 选项
 *
 * @author CH
 * @version 1.0.0
 */
public interface FailureConsumer<T> extends Consumer<T> {
    /**
     * 异常
     *
     * @param e 异常
     */
    void failure(Throwable e);
}

