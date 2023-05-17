package com.chua.quartz.support.scheduler;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.factory.ApplicationContextBuilder;
import com.chua.common.support.task.scheduler.AbstractTimeScheduler;
import com.chua.common.support.task.scheduler.Scheduled;
import com.chua.common.support.task.scheduler.TimeScheduler;
import com.chua.common.support.utils.JavassistUtils;
import com.chua.common.support.utils.StringUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author CH
 */
@Spi("quartz")
public class QuartzTimeScheduler extends AbstractTimeScheduler {
    /**
     * 调度工厂
     */
    private SchedulerFactory schedulerFactory;
    /**
     *
     */
    private static final String EXECUTE = "execute";
    /**
     * 任务组
     */
    private static final String JOB_GROUP_NAME = "FH_JOB_GROUP_NAME";
    /**
     * 触发器组
     */
    private static final String TRIGGER_GROUP_NAME = "FH_TRIGGER_GROUP_NAME";

    private final Map<String, Trigger> triggerMap = new HashMap<>();

    private Scheduler scheduler;

    public QuartzTimeScheduler() {
        super(JOB_GROUP_NAME);
        this.schedulerFactory = new StdSchedulerFactory();
    }

    public QuartzTimeScheduler(@NonNull SchedulerFactory schedulerFactory) {
        super(JOB_GROUP_NAME);
        this.schedulerFactory = schedulerFactory;
        //通过schedulerFactory来实例化一个Scheduler
        try {
            this.scheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void start(Executor executor, int core) throws Exception {
        if (null == scheduler) {
            //通过schedulerFactory来实例化一个Scheduler
            try {
                this.scheduler = schedulerFactory.getScheduler();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
        scheduler.start();
    }

    @Override
    public TimeScheduler unregister(Object entity) {
        Class<?> aClass = entity.getClass();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods) {
            Scheduled schedule = method.getDeclaredAnnotation(scheduleClass);
            if (null == schedule) {
                continue;
            }

            String taskName = analysisName(schedule, method);
            try {
                scheduler.deleteJob(JobKey.jobKey(taskName, JOB_GROUP_NAME));
            } catch (SchedulerException ignored) {
            }
        }
        return this;
    }

    @Override
    public TimeScheduler register(Object entity) {
        Class<?> aClass = getType(entity);
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

        JobDetail jobDetail = analysisJob(schedule, taskName, method, entity);
        Trigger trigger = analysisTrigger(schedule, taskName);

        if (null == jobDetail) {
            return this;
        }
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            if (!scheduler.isShutdown()) {
                scheduler.start(); // 启动
            }
        } catch (SchedulerException ignored) {
        }
        return this;
    }

    /**
     * 实体
     *
     * @param entity 实体
     * @return 类型
     */
    private Object getEntity(Object entity) {
        if (entity instanceof TypeDefinition) {
            return ((TypeDefinition) entity).getObject(ApplicationContextBuilder.newBuilder().build());
        }

        return entity;
    }

    /**
     * 类型
     *
     * @param entity 实体
     * @return 类型
     */
    private Class<?> getType(Object entity) {
        if (entity instanceof TypeDefinition) {
            return ((TypeDefinition) entity).getType();
        }

        return entity.getClass();
    }

    @Override
    public TimeScheduler change(String name, String cron) {
        if (StringUtils.isNullOrEmpty(name)) {
            this.cron = cron;
            return this;
        }
        // 通过SchedulerFactory构建Scheduler对象
        Trigger trigger = newTrigger().withIdentity(name, TRIGGER_GROUP_NAME)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron)).startNow().build();

        try {
            scheduler.rescheduleJob(TriggerKey.triggerKey(name, TRIGGER_GROUP_NAME), trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void stop() throws Exception {
        scheduler.shutdown();
    }

    @SneakyThrows
    @Override
    public void resume(String name) {
        scheduler.resumeJob(JobKey.jobKey(name));
    }

    @SneakyThrows
    @Override
    public void pause(String name) {
        scheduler.pauseJob(JobKey.jobKey(name));
    }

    /**
     * 任务
     *
     * @param schedule 注解
     * @param taskName 名称
     * @param method   方法
     * @param obj      对象
     * @return 任务
     */
    private JobDetail analysisJob(Scheduled schedule, String taskName, Method method, Object obj) {
        Class<? extends Job> job = createJob(method);
        if (null == job) {
            return null;
        }
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("method", method);
        jobDataMap.put("schedule", schedule);
        jobDataMap.put("obj", obj);
        jobDataMap.put("timeSchedule", this);

        return JobBuilder.newJob(job)
                .withIdentity(taskName)
                .setJobData(jobDataMap)
                .build();
    }

    /**
     * 创建任务
     *
     * @param method 方法
     * @return 任务
     */
    private Class<? extends Job> createJob(Method method) {
        ClassPool classPool = JavassistUtils.getClassPool();
        classPool.importPackage(JobExecutionContext.class.getName());
        classPool.importPackage(JobExecutionException.class.getName());
        classPool.importPackage(TypeDefinition.class.getName());
        classPool.importPackage(NewJob.class.getName());
        classPool.importPackage(Method.class.getName());

        try {
            CtClass ctClass = classPool.makeClass(method.getDeclaringClass().getName() + "$" + method.getName() + System.currentTimeMillis());
            ctClass.addInterface(classPool.get(NewJob.class.getName()));
            ctClass.addField(CtField.make("public Object obj;", ctClass));
            ctClass.addField(CtField.make("public Method method;", ctClass));
            ctClass.addMethod(CtMethod.make("public void execute()\n" +
                    "          throws Exception\n" +
                    "        {\n" +
                    "           try {\n" +
                    "               Object obj1 = obj;\n" +
                    "               if(obj instanceof TypeDefinition) {\n" +
                    "                   obj1 = ((TypeDefinition) obj).getObject(null);\n" +
                    "                } \n" +
                    "               method.invoke(obj1, new Object[method.getParameterCount()]);\n" +
                    "            } catch (Exception ignored) {\n" +
                    "            }" +
                    "        } ", ctClass));

            Object entity = JavassistUtils.toEntity(ctClass, classPool);
            if (null != entity) {
                return (Class<? extends Job>) entity.getClass();
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    /**
     * 触发器
     *
     * @param schedule 注解
     * @param taskName 名称
     * @return 触发器
     */
    private Trigger analysisTrigger(Scheduled schedule, String taskName) {
        return triggerMap.computeIfAbsent(taskName, it -> TriggerBuilder.newTrigger()
                .startAt(new Date())
                .withIdentity(taskName, TRIGGER_GROUP_NAME)
                .withSchedule(CronScheduleBuilder.cronSchedule(StringUtils.isNullOrEmpty(schedule.value()) ? cron : schedule.value()))
                .build());
    }
}
