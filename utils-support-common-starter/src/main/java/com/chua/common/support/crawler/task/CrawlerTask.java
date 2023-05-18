package com.chua.common.support.crawler.task;

/**
 * 任务
 *
 * @author chenhua
 */
public interface CrawlerTask extends Runnable {
    /**
     * 停止
     */
    void toStop();
}
