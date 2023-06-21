package com.chua.common.support.task.lmax;

import com.chua.common.support.function.NamedThreadFactory;
import com.chua.common.support.modularity.MsgEvent;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.utils.ThreadUtils;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * Disruptor
 * @author CH
 */
public class DisruptorFactory<T> {
    private final Disruptor<T> disruptor;
    /**
     * 对象工厂
     */
    private DisruptorObjectFactory<T> objectFactory;
    /**
     * 队列长度
     */
    private int bufferSize ;
    /**
     * 执行器
     */
    private ThreadFactory threadFactory;
    /**
     *生产者模式
     */
    private ProducerType producerType;
    /**
     * 策略
     */
    private WaitStrategy waitStrategy;

    public DisruptorFactory(DisruptorObjectFactory<T> objectFactory, int bufferSize, ThreadFactory threadFactory) {
        this(objectFactory, bufferSize , threadFactory, null, null);
    }

    public DisruptorFactory(DisruptorObjectFactory<T> objectFactory, int bufferSize) {
        this(objectFactory, bufferSize , null, null, null);

    }

    public DisruptorFactory(DisruptorObjectFactory<T> objectFactory) {
        this(objectFactory, 0 , null, null, null);
    }

    public DisruptorFactory(DisruptorObjectFactory<T> objectFactory, ThreadFactory threadFactory) {
        this(objectFactory, 0 , threadFactory, null, null);

    }

    public DisruptorFactory(DisruptorObjectFactory<T> objectFactory, int bufferSize, ThreadFactory threadFactory, ProducerType producerType, WaitStrategy waitStrategy) {
        this.objectFactory = objectFactory;
        this.bufferSize = 1 > bufferSize ? 1024 : bufferSize;
        this.threadFactory = ObjectUtils.defaultIfNull(threadFactory, new NamedThreadFactory("disruptor"));
        this.producerType = ObjectUtils.defaultIfNull(producerType, ProducerType.SINGLE);
        this.waitStrategy = ObjectUtils.defaultIfNull(waitStrategy, new YieldingWaitStrategy());
        this.disruptor = new Disruptor<>(this.objectFactory, this.bufferSize, this.threadFactory, this.producerType, this.waitStrategy);
    }


}
