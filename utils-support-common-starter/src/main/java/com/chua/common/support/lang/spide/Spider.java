package com.chua.common.support.lang.spide;

import com.chua.common.support.lang.spide.task.Task;

/**
 * 爬虫
 * @author CH
 */
@SuppressWarnings("ALL")
public interface Spider<Spider> extends Runnable, Task<Spider> {
    /**
     * 初始化
     * @return 构造器
     */
    public static SpiderBuilder newBuilder() {
        return new SpiderBuilder();
    }

    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void stop();

}
