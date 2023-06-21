package com.chua.common.support.task.lmax;

/**
 * 时间工厂
 * @author CH
 */
public interface DisruptorEventHandlerFactory<T> {
    /**
     * 获取事件
     * @param  name 名称
     * @return 事件
     */
    DisruptorEventHandler<T> getEventHandler(String name);
}
