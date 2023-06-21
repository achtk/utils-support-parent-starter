package com.chua.common.support.task.lmax;

import com.lmax.disruptor.EventFactory;

/**
 * 对象工厂用于生成对象
 * @author CH
 */
public interface DisruptorObjectFactory<T> extends EventFactory<T> {
}
