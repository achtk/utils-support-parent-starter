package com.chua.common.support.task.scheduler;

import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.task.scheduler.strategy.TimeFirstStrategy;
import com.chua.common.support.task.scheduler.strategy.TimeStrategy;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.ThreadUtils;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 调度
 *
 * @author CH
 */
public abstract class AbstractTimeScheduler implements TimeScheduler {

    private static final TimeStrategy FIRST = new TimeFirstStrategy();
    protected final AtomicBoolean state = new AtomicBoolean(false);
    protected final Map<String, TimeStrategy> nameAndStrategy = new ConcurrentHashMap<>();
    private final ServiceProvider<TimeStrategy> serviceProvider = ServiceProvider.of(TimeStrategy.class);

    protected Executor coreExecutor;
    /**
     * 表达式
     */
    protected String cron;
    /**
     * 名称
     */
    protected String name;
    /**
     * 执行器
     */
    protected Executor executor;
    /**
     * 核心数
     */
    protected int core = 1;

    protected Class<Scheduled> scheduleClass = Scheduled.class;
    @Getter
    private TimeSchedulerBuilder timeSchedulerBuilder;

    public AbstractTimeScheduler(String name) {
        this.name = name;
    }

    @Override
    public void stop() throws Exception {
        state.set(false);
        ThreadUtils.closeQuietly(executor);
    }

    @Override
    public void setCache(TimeSchedulerBuilder timeSchedulerBuilder) {
        this.timeSchedulerBuilder = timeSchedulerBuilder;
    }

    /**
     * 策略
     *
     * @param schedule 注解
     * @return 策略
     */
    protected TimeStrategy analysisStrategy(Scheduled schedule) {
        String strategy = schedule.strategy();
        return serviceProvider.getIfPresent(strategy).orElse(FIRST);
    }


    /**
     * 解析名称
     *
     * @param schedule 调度
     * @param method   名称
     * @return 名称
     */
    protected String analysisName(Scheduled schedule, Method method) {
        return StringUtils.isNullOrEmpty(schedule.name()) ? method.getName() : schedule.name();
    }

    /**
     * 获取策略
     *
     * @param name 名称
     * @return 策略
     */
    protected TimeStrategy getStrategy(String name) {
        return nameAndStrategy.get(name);
    }


}
