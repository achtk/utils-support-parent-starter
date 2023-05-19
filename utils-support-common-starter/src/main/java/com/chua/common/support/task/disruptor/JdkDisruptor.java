package com.chua.common.support.task.disruptor;

import com.chua.common.support.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * jdk
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/30
 */
@Slf4j
public class JdkDisruptor<E> extends AbstractDisruptor<E> implements Disruptor<E> {

    private Executor producer;
    private Executor consumer;
    private Queue<DisruptorMessage<E>> queue;
    private final AtomicLong count = new AtomicLong(0);

    @Override
    public void publish(E message) {
        if (null == message) {
            if (log.isDebugEnabled()) {
                log.warn("消息不能为空");
            }
            return;
        }
        this.producer.execute(() -> {
            this.queue.offer(new DisruptorMessage<E>(message, count.getAndIncrement(), false));
        });
    }

    @Override
    public void initial(Class<E> type, int bufferSize, ThreadFactory threadFactory, Executor executor, EntityFactory<E> eEntityFactory) {
        super.initial(type, bufferSize, threadFactory, executor, entityFactory);
        this.producer = ThreadUtils.newSingleThreadExecutor(threadFactory);
        this.consumer = Optional.ofNullable(executor).orElse(ThreadUtils.newFixedThreadExecutor(ThreadUtils.processor(), threadFactory));
        this.queue = new ConcurrentLinkedQueue<>(Collections.emptyList());
        this.type = type;
    }


    @Override
    public void start() {
        super.start();
        int processor = ThreadUtils.processor();
        int size = processor;
        for (int i = 0; i < size; i++) {
            this.consumer.execute(() -> {
                while (isRunning() || (!isRunning() && !queue.isEmpty())) {
                    DisruptorMessage<E> poll = this.queue.poll();
                    if (null != poll) {
                        for (EventActor<? super E> handler : handles) {
                            if (null != handler) {
                                try {
                                    Thread.sleep(20);
                                    handler.onEvent(poll.getElement(), poll.getSeq(), poll.isEndOfBatch());
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                }
            });
        }
    }


    @Override
    public void close() throws Exception {
        super.setClose();
        ThreadUtils.closeQuietly(producer);
        ThreadUtils.closeQuietly(consumer);
    }

}
