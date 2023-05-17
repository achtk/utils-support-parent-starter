package com.chua.common.support.function;

import java.util.function.Predicate;

/**
 * 启动器
 *
 * @author CH
 */
public interface Launcher extends AutoCloseable {
    /**
     * 启动
     *
     * @param args 参数
     * @throws Exception 异常
     */
    void run(String[] args) throws Exception;

    /**
     * 启动
     *
     * @throws Exception 异常
     */
    default void run() throws Exception {
        run(new String[0]);
    }

    /**
     * 启动
     *
     * @param args 参数
     * @throws Exception 异常
     */
    default void runAsync(String[] args) throws Exception {
        run(args);
    }

    /**
     * 启动
     *
     * @throws Exception 异常
     */
    default void runAsync() throws Exception {
        runAsync(new String[0]);
    }

    /**
     * 关闭
     *
     * @throws Exception 异常
     */
    void stop() throws Exception;

    /**
     * 关闭
     *
     * @throws Exception 异常
     */
    @Override
    default void close() throws Exception {
        stop();
    }

    /**
     * 关闭
     *
     * @param predicate 条件
     * @throws Exception 异常
     */
    default void stopIfPredicate(Predicate predicate) throws Exception {
        stop();
    }


}
