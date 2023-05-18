package com.chua.common.support.crawler;

import java.util.function.Predicate;

/**
 * 爬虫
 *
 * @author CH
 */
public interface Crawler extends AutoCloseable {

    /**
     * 开始
     *
     * @param async 是否异步
     * @throws Exception ex
     */
    void start(boolean async) throws Exception;

    /**
     * 开始
     *
     * @throws Exception ex
     */
    default void startSync() throws Exception {
        start(false);
    }

    /**
     * 开始
     *
     * @throws Exception ex
     */
    default void start() throws Exception {
        start(true);
    }

    /**
     * 停止
     *
     * @throws Exception ex
     */
    void stop() throws Exception;

    /**
     * 停止
     *
     * @param predicate 停止条件
     * @throws Exception ex
     */
    void stopIfPrediction(Predicate<Long> predicate) throws Exception;

    /**
     * 关闭
     * @throws Exception ex
     */
    @Override
    default void close() throws Exception {
        stop();
    }

    /**
     * 添加地址
     * @param s 地址
     */
    void addUrl(String s);
}
