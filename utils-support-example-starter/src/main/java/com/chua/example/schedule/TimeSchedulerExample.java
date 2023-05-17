package com.chua.example.schedule;

import com.chua.common.support.task.scheduler.Scheduled;
import com.chua.common.support.task.scheduler.TimeScheduler;
import com.chua.common.support.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务调度例子
 *
 * @author CH
 * @since 2021-11-08
 */
public class TimeSchedulerExample {

    public static void main(String[] args) throws Exception {
        //通过spi扩展 {@link com.chua.tools.common.scheduler2.TimeScheduler}

        //创建一个任务调度, 默认采用quartz
        TimeScheduler timeScheduler = TimeScheduler
                //创建 jdk实现的任务调度
                .builder("quartz")
                .build();

        //注册一个任务对象, 方式1
        //注册的对象的方法, 必须包含{@link com.chua.tools.common.scheduler.Schedule}注解
        //注解可以设备调度的名称, 默认以方法名作为调度名称, 调度名称用于控制调度
        //  timeScheduler.register(new EntityDefinition(new TestSchedule()));
        //注册一个任务对象, 方式2
        //注册的对象的方法, 必须包含{@link com.chua.tools.common.scheduler.Schedule}注解
        //注解可以设备调度的名称, 默认以方法名作为调度名称, 调度名称用于控制调度
        timeScheduler.register(new TestSchedule());
        //注册一个脚本
        //参数1: 脚本对应的接口
        //脚本对应的接口, 必须包含{@link com.chua.tools.common.scheduler.Schedule}注解
        //参数3: 脚本的URL
        //注解可以设备调度的名称, 默认以方法名作为调度名称, 调度名称用于控制调度
//        timeScheduler.register(new ScriptDefinition(TDemoInfo.class, ClassUtils.getDefaultClassLoader(), ResourceProvider.of("classpath:TDemoInfoImpl.java").getResource().getUrl()));

        ThreadUtils.sleepSecondsQuietly(10);
        //改变test任务的时间
        timeScheduler.change("test", "0/1 * * * * ?");
        ThreadUtils.sleepSecondsQuietly(20);
        //改变test任务的时间
        timeScheduler.change("test", "0/3 * * * * ?");
        ThreadUtils.sleepSecondsQuietly(10);
        //停止调度
        timeScheduler.stop();


    }


    @Slf4j
    public static class TestSchedule {

        @Scheduled(value = "0/2 * * * * ?")
        public void test() {
            log.info("执行方法");
        }

        @Scheduled(value = "0/2 * * * * ?")
        public void test1() {
            log.info("执行方法11");
        }
    }
}
