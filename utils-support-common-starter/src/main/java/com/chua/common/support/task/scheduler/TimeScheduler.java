package com.chua.common.support.task.scheduler;

import com.chua.common.support.spi.ServiceProvider;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * 调度
 *
 * @author CH
 */
public interface TimeScheduler {

    /**
     * 开始
     *
     * @param executor Executor
     * @param core     core
     * @throws Exception ex
     */
    void start(Executor executor, int core) throws Exception;

    /**
     * 注销实体
     *
     * @param entity 实体
     * @return this
     */
    TimeScheduler unregister(Object entity);

    /**
     * 注册实体(调用的方法需要注册 {@link Scheduled})
     *
     * @param entity 实体
     * @return this
     * @see Scheduled
     */
    TimeScheduler register(Object entity);

    /**
     * 注册实体(调用的方法需要注册
     *
     * @param entity 实体
     * @param method 方法
     * @return this
     */
    TimeScheduler register(Object entity, Method method);

    /**
     * 修改任务时间
     *
     * @param name 任务名称, {@link Scheduled#name()} 或者是注解对应的方法名称
     * @param cron 任务时间
     * @return this
     */
    TimeScheduler change(String name, String cron);

    /**
     * 停止
     *
     * @throws Exception ex
     */
    void stop() throws Exception;

    /**
     * 暂停
     *
     * @param name 名称
     */
    void pause(String name);


    /**
     * 恢复
     *
     * @param name 名称
     */
    void resume(String name);

    /**
     * 创建构造器
     *
     * @return 构造器
     */
    static TimeSchedulerBuilder builder() {
        return builder("quartz");
    }

    /**
     * 创建构造器
     *
     * @param type 类型
     * @return 构造器
     */
    static TimeSchedulerBuilder builder(String type) {
        return new TimeSchedulerBuilder(type);
    }


    class TimeSchedulerBuilder {
        private final TimeScheduler timeScheduler;
        private String cron;
        private int core;
        private Executor executor;

        public TimeSchedulerBuilder(String type) {
            this.timeScheduler = ServiceProvider.of(TimeScheduler.class).getNewExtension(type, "jdk");
        }

        /**
         * 表达式
         *
         * @param cron 表达式
         * @return this
         */
        public TimeSchedulerBuilder cron(String cron) {
            this.cron = cron;
            timeScheduler.change("", cron);
            return this;
        }

        /**
         * 设置执行器
         *
         * @param executor 执行器
         * @return this
         */
        public TimeSchedulerBuilder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * 设置核心
         *
         * @param core 核心数
         * @return this
         */
        public TimeSchedulerBuilder core(int core) {
            this.core = core;
            return this;
        }

        public TimeScheduler build() {
            timeScheduler.setCache(this);
            //PreconditionUtils.checkArgument(null != cron, "cron不能为空");
            try {
                timeScheduler.start(executor, core);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return timeScheduler;
        }

    }

    void setCache(TimeSchedulerBuilder timeSchedulerBuilder);

}
