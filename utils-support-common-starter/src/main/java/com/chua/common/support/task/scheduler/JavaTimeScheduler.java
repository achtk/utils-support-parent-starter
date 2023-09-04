package com.chua.common.support.task.scheduler;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiDefault;
import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.function.NamedThreadFactory;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.task.scheduler.expression.Cron2Expression;
import com.chua.common.support.utils.ThreadUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author CH
 * @since 2022-02-15
 */
@Spi("jdk")
@SpiDefault
public class JavaTimeScheduler extends AbstractTimeScheduler {

    private final Object LOCK = new Object();
    protected final Map<String, List<TimeTask>> nameAndEntity = new ConcurrentHashMap<>();
    protected final Map<String, List<TimeTask>> pause = new ConcurrentHashMap<>();

    private Date beforeDate;

    public JavaTimeScheduler() {
        super("JDK");
    }

    public JavaTimeScheduler(String name) {
        super(name);
    }

    @Override
    public void start(Executor executor, int core) throws Exception {
        this.state.set(true);
        this.executor = executor;
        if (null == executor) {
            this.executor = ThreadUtils.newFixedThreadExecutor(Math.max(core, 1), "jdk-schedule");
        }

        this.executor.execute(() -> {
            while (state.get()) {
                for (List<TimeTask> timeTasks : nameAndEntity.values()) {
                    for (TimeTask timeTask : timeTasks) {
                        if (!timeTask.getIsRunning().get()) {
                            timeTask.run();
                        }
                    }
                }

            }
        });
    }


    @Override
    public TimeScheduler unregister(Object entity) {
        if (!nameAndEntity.containsKey(name)) {
            return this;
        }
        List<TimeTask> timeTasks = nameAndEntity.get(name);
        for (TimeTask timeTask : timeTasks) {
            try {
                timeTask.close();
            } catch (Exception ignored) {
            }
        }
        nameAndEntity.remove(name);
        nameAndStrategy.remove(name);
        return this;
    }

    @Override
    public TimeScheduler register(Object entity) {
        Class<?> aClass = analysisType(entity);
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods) {
            register(entity, method);
        }
        return this;
    }

    @Override
    public TimeScheduler register(Object entity, Method method) {
        Scheduled schedule = method.getDeclaredAnnotation(scheduleClass);
        if (null == schedule) {
            return this;
        }
        String taskName = analysisName(schedule, method);
        TimeTask timeTask = analysisTask(schedule, method, entity);
        nameAndEntity.computeIfAbsent(taskName
                        , it -> new SortedArrayList<>(Comparator.comparing(TimeTask::getOrder)))
                .add(timeTask);
        nameAndStrategy.put(taskName, analysisStrategy(schedule));
        return this;
    }

    /**
     * 解析实体
     *
     * @param schedule 调度
     * @param method   方法
     * @param obj      实体
     * @return 任务
     */
    protected TimeTask analysisTask(Scheduled schedule, Method method, Object obj) {
        method.setAccessible(true);
        TimeTask timeTask = new TimeTask();
        timeTask.setMethod(method);
        timeTask.setObj(obj);
        timeTask.setOrder(schedule.order());
        timeTask.setName(analysisName(schedule, method));
        timeTask.setCron(schedule.value());
        timeTask.setStartDate(new Date());
        timeTask.setInitialDate(new Date());
        timeTask.setCronExpress(Cron2Expression.parse(schedule.value()));

        return timeTask;
    }

    /**
     * 获取类型
     *
     * @param entity 实体
     * @return 类型
     */
    private Class<?> analysisType(Object entity) {
        if (entity instanceof TypeDefinition) {
            TypeDefinition entityDefinition = (TypeDefinition) entity;
            return entityDefinition.getType();
        }
        return entity.getClass();
    }

    @Override
    public TimeScheduler change(String name, String cron) {
        if (!nameAndEntity.containsKey(name)) {
            return this;
        }
        List<TimeTask> timeTasks = nameAndEntity.get(name);
        for (TimeTask timeTask : timeTasks) {
            timeTask.setCronExpress(Cron2Expression.parse(cron));
        }
        return this;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        for (List<TimeTask> value : nameAndEntity.values()) {
            for (TimeTask timeTask : value) {
                timeTask.close();
            }
        }
    }

    @Override
    public void pause(String name) {
        List<TimeTask> timeTasks = nameAndEntity.get(name);
        pause.put(name, timeTasks);
        nameAndEntity.remove(timeTasks);
    }

    @Override
    public void resume(String name) {
        List<TimeTask> timeTasks = pause.get(name);
        nameAndEntity.put(name, timeTasks);
        pause.remove(timeTasks);
    }

    /**
     * 任务
     */
    @Data
    @Slf4j
    protected static final class TimeTask implements AutoCloseable {
        /**
         * 表达式
         */
        private String cron;
        /**
         * 任务对象
         */
        private Object obj;
        /**
         * 名称
         */
        private String name;
        /**
         * 方法
         */
        private Method method;

        /**
         * 优先级
         */
        private int order;
        /**
         * 初始化时间
         */
        private Date initialDate;
        /**
         * 开始时间
         */
        private Date startDate;
        /**
         * 完成时间
         */
        private Date completedDate;
        private Cron2Expression cronExpress;
        /**
         * 下一次时间
         */
        private Date nextDate;
        private ScheduledFuture<?> schedule;

        private AtomicBoolean isRunning = new AtomicBoolean(false);

        private ScheduledExecutorService scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("jdk"));

        @Override
        public void close() throws Exception {
            scheduledExecutorService.shutdownNow();
        }


        public void run() {
            if (isRunning.get()) {
                return;
            }
            isRunning.set(true);
            runInvoke();
        }

        private void runInvoke() {
            if (nextDate == null) {
                scheduledExecutorService.schedule(() -> {
                    invoke();
                    this.completedDate = new Date();
                    ZonedDateTime dateTime = ZonedDateTime.ofInstant(completedDate.toInstant(), ZoneId.systemDefault());
                    ZonedDateTime next = cronExpress.next(dateTime);
                    nextDate = Date.from(next.toInstant());
                    runInvoke();
                }, 0, TimeUnit.SECONDS);
                return;
            }
            long nextTime = nextDate.getTime() / 1000 - completedDate.getTime() / 1000;
            scheduledExecutorService.schedule(() -> {
                invoke();
                this.completedDate = new Date();
                ZonedDateTime dateTime = ZonedDateTime.ofInstant(completedDate.toInstant(), ZoneId.systemDefault());
                ZonedDateTime next = cronExpress.next(dateTime);
                nextDate = Date.from(next.toInstant());
                runInvoke();
            }, nextTime, TimeUnit.SECONDS);
        }

        private void invoke() {
            try {
                method.invoke(getObject(), new Object[method.getParameterCount()]);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        private Object getObject() {
            if (obj instanceof TypeDefinition) {
                return ((TypeDefinition) obj).newInstance(null);
            }
            return obj;
        }

    }

}
