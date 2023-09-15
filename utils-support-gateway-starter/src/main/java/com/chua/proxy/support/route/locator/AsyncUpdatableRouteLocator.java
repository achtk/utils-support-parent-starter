package com.chua.proxy.support.route.locator;

import com.chua.common.support.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 异步可更新路由定位器
 *
 * @author CH
 */
@Slf4j
public abstract class AsyncUpdatableRouteLocator extends UpdatableRouteLocator {

    public static final int DEFAULT_UPDATE_INTERVAL = 10;

    private final AtomicBoolean updated = new AtomicBoolean(false);

    private final int periodInSeconds;

    private final ScheduledExecutorService scheduledExecutorService;

    protected AsyncUpdatableRouteLocator() {
        this(DEFAULT_UPDATE_INTERVAL);
    }

    protected AsyncUpdatableRouteLocator(int updatePeriodInSeconds) {
        this.scheduledExecutorService = ThreadUtils.newSingleThreadScheduledExecutor();
        this.periodInSeconds = updatePeriodInSeconds;
    }

    @Override
    public synchronized void update() {
        updated.set(true);
    }

    @Override
    public void start() {
        if (scheduledExecutorService.isShutdown() || scheduledExecutorService.isTerminated()) {
            log.error("Can not start {}} because it's already started.", this.getClass().getSimpleName());
            return;
        }
        log.info("Start {}.", this.getClass().getSimpleName());
        // initialDelay 设置为2秒，2秒的时间应该足够gateway程序启动，完成向dashboard的注册，并发送第一个心跳
        // 一般收到第一个心跳的响应后就会有API需要同步，会执行update()
        scheduledExecutorService.scheduleAtFixedRate(this::doUpdate, 2, periodInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        if (!scheduledExecutorService.isShutdown()) {
            log.info("Stop {}.", this.getClass().getSimpleName());
            scheduledExecutorService.shutdownNow();
        }
    }

    @Override
    protected void doUpdate() {
        if (!updated.compareAndSet(true, false)) {
            return;
        }

        super.doUpdate();
    }

}
