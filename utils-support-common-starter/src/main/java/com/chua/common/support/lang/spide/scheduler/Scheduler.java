package com.chua.common.support.lang.spide.scheduler;

import com.chua.common.support.lang.spide.request.Request;
import com.chua.common.support.lang.spide.task.Task;

/**
 * URL存储器
 * @author CH
 */
public interface Scheduler<Spider> {

    /**
     * 添加请求
     *
     * @param request request
     * @param task task
     */
    void push(Request request, Task<Spider> task);

    /**
     * 抽取请求
     *
     * @param task the task of spider
     * @return the url to crawl
     */
    Request poll(Task<Spider> task);
}
