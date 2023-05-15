package com.chua.common.support.function;

/**
 * 销毁
 *
 * @author CH
 */
public interface DisposableAware extends AutoCloseable {
    /**
     * 销毁
     */
    void destroy();

    /**
     * close
     *
     * @throws Exception ex
     */
    @Override
    default void close() throws Exception {
        destroy();
    }
}
