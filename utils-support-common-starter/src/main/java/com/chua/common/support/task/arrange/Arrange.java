package com.chua.common.support.task.arrange;

import com.chua.common.support.task.arrange.async.executor.Async;

import java.util.concurrent.ExecutionException;

/**
 * 编排
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/30
 */
public interface Arrange {

    /**
     * 添加worker
     *
     * @param worker worker
     * @return this
     */
    <T, V> Arrange addWorker(Worker<T, V> worker);

    /**
     * 添加worker
     *
     * @param name   name
     * @param worker worker
     * @return this
     */
    <T, V> Arrange addAfter(String name, Worker<T, V> worker);

    /**
     * 添加worker
     *
     * @param name  name
     * @param name2 name2
     * @return this
     */
    <T, V> Arrange addAfter(String name, String name2);

    /**
     * 开始
     *
     * @param timeout 超时时间
     * @throws ExecutionException 执行异常
     * @throws InterruptedException 执行异常
     */
    void start(int timeout) throws ExecutionException, InterruptedException;

    /**
     * 结束
     */
    void stop();

    /**
     * 获取线程
     * @return 线程
     */
    default String getThreadCount() {
        return Async.getThreadCount();
    }

    /**
     * 打印结构
     *
     * @return 打印结构
     */
    String print();
}
