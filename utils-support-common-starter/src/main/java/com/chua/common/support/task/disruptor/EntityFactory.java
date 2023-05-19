package com.chua.common.support.task.disruptor;


/**
 * 实体工厂
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/29
 */
public interface EntityFactory<T> {
    /**
     * Implementations should instantiate an event object, with all memory already allocated where possible.
     *
     * @return 实体
     */
    T newInstance();
}
