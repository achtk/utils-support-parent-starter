package com.chua.common.support.lang.thread;

/**
 * 线程任务
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/8
 */
public interface ThreadTask {
    /**
     * 执行方法
     *
     * @param value 参数
     * @return 结果
     */
    Object execute(Object value);

}
