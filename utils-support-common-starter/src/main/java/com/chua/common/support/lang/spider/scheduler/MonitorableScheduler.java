package com.chua.common.support.lang.spider.scheduler;

import com.chua.common.support.lang.spider.Task;

/**
 * The scheduler whose requests can be counted for monitor.
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public interface MonitorableScheduler extends Scheduler {
    /**
     * 获取请求数量
     * @param task 任务
     * @return 数量
     */

    int getLeftRequestsCount(Task task);
    /**
     * 获取总请求数量
     * @param task 任务
     * @return 数量
     */
    int getTotalRequestsCount(Task task);

}