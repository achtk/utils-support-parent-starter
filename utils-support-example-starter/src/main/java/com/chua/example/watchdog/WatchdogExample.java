package com.chua.example.watchdog;


import com.chua.common.support.lang.watchdog.ExecuteWatchdog;
import com.chua.common.support.utils.ThreadUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author CH
 * @since 2021-12-07
 */
public class WatchdogExample {

    public static void main(String[] args) throws Exception {
        ExecuteWatchdog executeWatchdog = new ExecuteWatchdog(3000);
        executeWatchdog.observer(new Consumer<ExecutorService>() {
            @Override
            public void accept(ExecutorService o) {
                System.out.println("查询超时开始关闭线程池");
                o.shutdownNow();
                System.out.println("\t关闭完成");
            }
        });

        ExecutorService executorService = ThreadUtils.newFixedThreadExecutor(1);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //监听对象
        executeWatchdog.monitor(executorService);

        executorService.execute(() -> {
            ThreadUtils.sleepSecondsQuietly(Integer.MAX_VALUE);
        });

//        countDownLatch.await(Integer.MAX_VALUE, TimeUnit.SECONDS);


//        executeWatchdog.stop();
//        try {
//            executeWatchdog.checkException();
//        } catch (Exception ignored) {
//        }
    }
}
