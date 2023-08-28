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
    String getUuid();

    /**
     * site of a task
     *
     * @return site
     */
    Site getSite();

}
