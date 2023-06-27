package com.chua.common.support.task.lmax;

import com.chua.common.support.unit.TimeSize;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.utils.Preconditions;
import com.chua.common.support.utils.ThreadUtils;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Disruptor
 * @author CH
 */
@SuppressWarnings("ALL")
public class DisruptorFactory<T> implements AutoCloseable {
    private final Disruptor<T> disruptor;
    private DisruptorEventHandlerFactory<T> handlerFactory;

    private final AtomicBoolean status = new AtomicBoolean(false);
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
    private Executor executor;
    /**
     *生产者模式
     */
    private ProducerType producerType;
    /**
     * 策略
     */
    private WaitStrategy waitStrategy;

    public DisruptorFactory(DisruptorEventHandlerFactory<T> handlerFactory, DisruptorObjectFactory<T> objectFactory, int bufferSize, Executor executor) {
        this(handlerFactory, objectFactory, bufferSize , executor, null, null);
    }

    public DisruptorFactory(DisruptorEventHandlerFactory<T> handlerFactory, DisruptorObjectFactory<T> objectFactory, int bufferSize) {
        this(handlerFactory, objectFactory, bufferSize , null, null, null);

    }

    public DisruptorFactory(DisruptorEventHandlerFactory<T> handlerFactory, DisruptorObjectFactory<T> objectFactory) {
        this(handlerFactory, objectFactory, 0 , null, null, null);
    }

    public DisruptorFactory(DisruptorEventHandlerFactory<T> handlerFactory, DisruptorObjectFactory<T> objectFactory, WaitStrategy waitStrategy) {
        this(handlerFactory, objectFactory, 0 , null, null, waitStrategy);

    }

    public DisruptorFactory(DisruptorEventHandlerFactory<T> handlerFactory, DisruptorObjectFactory<T> objectFactory, Executor executor) {
        this(handlerFactory, objectFactory, 0 , executor, null, null);

    }

    public DisruptorFactory(DisruptorEventHandlerFactory<T> handlerFactory, DisruptorObjectFactory<T> objectFactory, int bufferSize, Executor executor, ProducerType producerType, WaitStrategy waitStrategy) {
        this.handlerFactory = handlerFactory;
        this.objectFactory = objectFactory;
        this.bufferSize = 1 > bufferSize ? 1024 : bufferSize;
        this.executor = ObjectUtils.defaultIfNull(executor, ThreadUtils.newProcessorThreadExecutor("disruptor"));
        this.producerType = ObjectUtils.defaultIfNull(producerType, ProducerType.SINGLE);
        this.waitStrategy = ObjectUtils.defaultIfNull(waitStrategy, new YieldingWaitStrategy());
        this.disruptor = new Disruptor<>(this.objectFactory, this.bufferSize, this.executor, this.producerType, this.waitStrategy);
    }


    public DisruptorGroup<T> handleEventsWith(String name) {
        return new DisruptorGroup<>(disruptor, handlerFactory, disruptor.handleEventsWith(handlerFactory.getEventHandler(name)));
    }


    public DisruptorGroup<T> after(String... names) {
        List<DisruptorEventHandler<T>> rs = new LinkedList<>();
        for (String name : names) {
            rs.add(Preconditions.checkNotNull(handlerFactory.getEventHandler(name)));
        }
        return new DisruptorGroup<>(disruptor, handlerFactory, disruptor.after(rs.toArray(new DisruptorEventHandler[0])));
    }


    public void start() {
        disruptor.start();
        status.set(true);
    }


    @Override
    public void close() throws Exception {
        status.set(false);
        ThreadUtils.closeQuietly(executor);
        disruptor.shutdown();
    }

    /**
     * 发送消息
     * @param sequence 序号
     */
    public void publish(int sequence) {
        RingBuffer<T> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publish(sequence);
    }

    /**
     * 关闭
     * @param timeSize 时间
     */
    public void shutdown(TimeSize timeSize) {
        try {
            disruptor.shutdown(timeSize.toSecond(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public void waitFor(TimeSize timeSize, Supplier<Boolean> supplier, Consumer<Void> consumer) {
        long startTime = System.currentTimeMillis();
        while (status.get()) {
            ThreadUtils.sleepSecondsQuietly(1);
            if(supplier.get()) {
                try {
                    consumer.accept(null);
                    close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            if(System.currentTimeMillis() > startTime + timeSize.toMillis()) {
                try {
                    consumer.accept(null);
                    close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
