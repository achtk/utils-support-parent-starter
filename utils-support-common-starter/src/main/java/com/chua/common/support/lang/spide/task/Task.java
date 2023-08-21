package com.chua.common.support.lang.spide.task;

/**
 * 爬虫任务
 *
 * @author CH
 */
public interface Task<Spider> {


    /**
     * 任务ID
     *
     * @return uuid
     */
    String getUUID();

    /**
     * spider
     * @return spider
     */
    Spider getSpider();
}
