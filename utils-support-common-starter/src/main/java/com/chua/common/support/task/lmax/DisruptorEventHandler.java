package com.chua.common.support.task.lmax;

import com.lmax.disruptor.EventHandler;

/**
 * 回调
 * @author CH
 */
public interface DisruptorEventHandler<T> extends EventHandler<T> {
}
