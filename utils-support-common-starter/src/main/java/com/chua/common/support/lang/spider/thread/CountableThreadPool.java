package com.chua.common.support.lang.spider.thread;

import com.chua.common.support.utils.ThreadUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread pool for workers.<br><br>
 * Use {@link ExecutorService} as inner implement. <br><br>
 * New feature: <br><br>
 * 1. Block when thread pool is full to avoid poll many urls without process. <br><br>
 * 2. Count of thread alive for monitor.
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public class CountableThreadPool {

    private final int threadNum;

    private final AtomicInteger threadAlive = new AtomicInteger();

    private final ReentrantLock reentrantLock = new ReentrantLock();

    private final Condition condition = reentrantLock.newCondition();

    public CountableThreadPool(int threadNum) {
        this.threadNum = threadNum;
        this.executorService = ThreadUtils.newFixedThreadExecutor(threadNum);
    }

    public CountableThreadPool(int threadNum, ExecutorService executorService) {
        this.threadNum = threadNum;
        this.executorService = executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public int getThreadAlive() {
        return threadAlive.get();
    }

    public int getThreadNum() {
        return threadNum;
    }

    private ExecutorService executorService;

    public void execute(final Runnable runnable) {


        if (threadAlive.get() >= threadNum) {
            reentrantLock.lock();
            try {
                while (threadAlive.get() >= threadNum) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                    }
                }
            } finally {
                reentrantLock.unlock();
            }
        }
        threadAlive.incrementAndGet();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    reentrantLock.lock();
                    try {
                        threadAlive.decrementAndGet();
                        condition.signal();
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            }
        });
    }

    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    public void shutdown() {
        executorService.shutdown();
    }


}
