package com.chua.common.support.task.disruptor;

import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 分发器
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/30
 */
public abstract class AbstractDisruptor<E> implements Disruptor<E> {

    protected Executor executor;
    protected final List<EventActor<? super E>> handles = new ArrayList<>();
    protected AtomicBoolean isRunning = new AtomicBoolean(false);
    protected Class<E> type;
    protected final AtomicLong count = new AtomicLong(0);
    protected ThreadFactory threadFactory;
    protected int bufferSize;
    protected int process = Runtime.getRuntime().availableProcessors();
    protected EntityFactory<E> entityFactory;

    @Override
    public void handleEventsWith(EventActor<? super E> eventActor) {
        this.handles.add(eventActor);
    }

    @Override
    public int statue() {
        return isRunning.get() ? 1 : 0;
    }

    @Override
    public void start() {
        isRunning.set(true);
    }

    @Override
    public void publish(Consumer<E> consumer) {
        E forObject = ClassUtils.forObject(type);
        consumer.accept(forObject);
        this.publish(forObject);
    }

    @Override
    public void close() throws Exception {
        isRunning.set(false);
        ThreadUtils.closeQuietly(executor);
    }

    @Override
    public void initial(Class<E> type, int bufferSize, ThreadFactory threadFactory, Executor executor, EntityFactory<E> entityFactory) {
        this.type = type;
        this.bufferSize = bufferSize;
        this.threadFactory = threadFactory;
        this.executor = executor;
        this.entityFactory = entityFactory;
    }

    /**
     * 是否在运行
     *
     * @return 是否在运行
     */
    protected boolean isRunning() {
        return isRunning.get();
    }

    /**
     * 关闭
     */
    protected void setClose() {
        isRunning.set(false);
    }
}
