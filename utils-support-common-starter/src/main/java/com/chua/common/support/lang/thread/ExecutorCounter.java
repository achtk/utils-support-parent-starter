package com.chua.common.support.lang.thread;


import com.chua.common.support.function.FailureConsumer;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * 线程计数器
 *
 * @author CH
 * @version 1.0.0
 */
public interface ExecutorCounter {

    /**
     * 线程池
     *
     * @return 线程池
     */
    ExecutorService executorService();

    /**
     * 添加runnable
     *
     * @param runnable runnable
     * @return this
     */
    ExecutorCounter combine(ThreadTask runnable);

    /**
     * 添加runnable
     *
     * @param runnable runnable
     * @return this
     */
    default ExecutorCounter combine(Runnable runnable) {
        return combine(it -> {
            runnable.run();
            return null;
        });
    }

    /**
     * 添加 callable
     *
     * @param callable callable
     * @return this
     */
    default ExecutorCounter combine(Callable callable) {
        return combine(it -> {
            try {
                return callable.call();
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * 添加runnable
     *
     * @param num      数量
     * @param runnable runnable
     * @return this
     */
    ExecutorCounter forEach(int num, ThreadTask runnable);

    /**
     * 全部任务完成时完成
     */
    void allOfComplete();

    /**
     * 有任务完成时完成
     *
     * @param result 完成结果
     */
    void whenOfComplete(FailureConsumer<Object> result);

    /**
     * 任意任务完成时完成
     */
    void anyOfComplete();

    /**
     * 全部任务完成时完成
     *
     * @param consumer 消费者
     */
    void allOfComplete(FailureConsumer<List<Object>> consumer);

    /**
     * {unit}任务完成时完成
     *
     * @param unit 数量
     */
    void unitOfComplete(int unit);

    /**
     * {unit}任务完成时完成
     *
     * @param unit     数量
     * @param consumer 消费者
     */
    void unitOfComplete(int unit, Consumer<List<Object>> consumer);

    /**
     * 任意任务完成时完成
     *
     * @param consumer 消费者
     */
    void anyOfComplete(Consumer<List<Object>> consumer);

    /**
     * 运行
     */
    void run();

    /**
     * 运行
     *
     * @param runnable runnable
     */
    void run(Runnable runnable);

    /**
     * 运行
     *
     * @param callable runnable
     */
    void run(Callable callable);
}
