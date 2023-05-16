package com.chua.common.support.monitor;

import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.NetAddress;
import com.chua.common.support.utils.ThreadUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 监听
 *
 * @author CH
 */
public abstract class AbstractMonitor implements Monitor {

    private final Map<Class<?>, List<Listener>> listMap = new ConcurrentHashMap<>();
    protected final AtomicBoolean status = new AtomicBoolean(false);
    protected int interval;
    protected MonitorConfiguration configuration;
    protected ExecutorService executorService;
    protected NetAddress netAddress;

    @Override
    public Monitor addListener(Listener listener) {
        Type[] actualTypeArguments = ClassUtils.getActualTypeArguments(listener.getClass());
        if (actualTypeArguments.length == 0) {
            listMap.computeIfAbsent(NotifyMessage.class, it -> new LinkedList<>()).add(listener);
            return this;
        }
        Class<?> superClass = (Class<?>) actualTypeArguments[0];
        listMap.computeIfAbsent(superClass, it -> new LinkedList<>()).add(listener);

        return this;
    }

    @Override
    public void start() {
        status.set(true);
        preStart();
        executorService.submit(this::afterStart);
    }

    @Override
    public void stop() {
        status.set(false);
        preStop();
        afterStop();
        executorService.shutdownNow();
    }

    @Override
    public Monitor configuration(MonitorConfiguration configuration) {
        this.executorService = Optional.ofNullable(configuration.executorService()).orElse(ThreadUtils.newSingleThreadExecutor("monitor"));
        this.interval = configuration.interval();
        this.configuration = configuration;
        this.netAddress = NetAddress.of(configuration.url());
        return this;
    }

    /**
     * 通知
     *
     * @param e 异常
     */
    protected void notifyMessage(Throwable e) {
        notifyMessage(new NotifyMessage(e).setMessage(e.getMessage()));
    }

    /**
     * 通知
     *
     * @param message 消息
     */
    protected void notifyMessage(NotifyMessage message) {
        List<Listener> listeners = new LinkedList<>();
        Class<? extends NotifyMessage> aClass1 = message.getClass();
        Set<Class<?>> superType = ClassUtils.getSuperType(aClass1);
        CollectionUtils.addAll(listeners, listMap.get(aClass1));
        for (Class<?> aClass : superType) {
            CollectionUtils.addAll(listeners, listMap.get(aClass));
        }

        if (null == listeners) {
            return;
        }

        executorService.execute(() -> {
            for (Listener listener : listeners) {
                listener.onEvent(message);
            }
        });
    }

    /**
     * 是否启动
     *
     * @return 是否启动
     */
    protected boolean isRunning() {
        return status.get();
    }

    /**
     * 启动
     */
    public abstract void preStart();

    /**
     * 启动
     */
    public abstract void afterStart();

    /**
     * 停止
     */
    public abstract void preStop();

    /**
     * 停止
     */
    public abstract void afterStop();
}
