package com.chua.quartz.support.scheduler;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * job
 *
 * @author CH
 */
public interface NewJob extends Job {

    /**
     * 执行
     *
     * @throws Exception 异常
     */
    void execute() throws Exception;

    /**
     * 执行
     *
     * @param context 请求参数
     * @throws JobExecutionException 异常
     */
    @Override
    default void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Class<? extends NewJob> aClass = this.getClass();
        for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
            try {
                Field declaredField = aClass.getDeclaredField(entry.getKey());
                declaredField.setAccessible(true);
                declaredField.set(this, entry.getValue());
            } catch (Exception ignored) {
            }
        }
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
