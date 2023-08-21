package com.chua.common.support.lang.spider;

/**
 * Interface for identifying different tasks.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @seecom.chua.common.support.lang.spider.scheduler.Scheduler
 * @seecom.chua.common.support.lang.spider.pipeline.Pipeline
 * @since 0.1.0
 */
public interface Task {

    /**
     * unique id for a task.
     *
     * @return uuid
     */
    public String getUUID();

    /**
     * site of a task
     *
     * @return site
     */
    public Site getSite();

}
