package com.chua.example.lock;

import com.chua.common.support.lang.lock.FileLock;
import com.chua.common.support.lang.lock.Lock;
import com.chua.common.support.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author CH
 * @since 2022-05-30
 */
public class LockExample {

    public static void main(String[] args) {
        Lock lock = new FileLock("test");
        AtomicBoolean run = new AtomicBoolean(true);
        List<String> index = new ArrayList<>();
        ExecutorService executorService = ThreadUtils.newFixedThreadExecutor(3, "test", () -> {
            return new Runnable() {
                @Override
                public void run() {
                    while (run.get()) {
                        if (lock.lock(10000)) {
                            try {
                                index.add(Thread.currentThread().getName());
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } finally {
                                lock.unlock();
                            }
                        }
                    }
                }
            };
        });
        ThreadUtils.sleepSecondsQuietly(1000);
        run.set(false);
        ThreadUtils.closeQuietly(executorService);

        System.out.println(index.contains("test-0-1"));

    }
}
