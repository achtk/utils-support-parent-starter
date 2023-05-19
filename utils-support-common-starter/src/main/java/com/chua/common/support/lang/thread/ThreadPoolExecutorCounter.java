package com.chua.common.support.lang.thread;

import com.chua.common.support.function.FailureConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * 线程池计数器
 *
 * @author CH
 * @version 1.0.0
 */
@Slf4j
public class ThreadPoolExecutorCounter implements ExecutorCounter, AutoCloseable {

    private final ExecutorService executorService;
    private final List<ThreadTask> taskList = new LinkedList<>();

    public ThreadPoolExecutorCounter(ExecutorService executorService) {
        this.executorService = executorService;
    }


    @Override
    public ExecutorService executorService() {
        return executorService;
    }

    @Override
    public ExecutorCounter combine(ThreadTask runnable) {
        this.taskList.add(runnable);
        return this;
    }

    @Override
    public ExecutorCounter forEach(int num, ThreadTask runnable) {
        IntStream.range(0, num).forEach(it -> {
            this.combine(runnable);
        });
        return this;
    }


    @Override
    public void allOfComplete() {
        CountDownLatch latch = new CountDownLatch(taskList.size());
        for (ThreadTask task : taskList) {
            executorService.execute(() -> {
                try {
                    task.execute(null);
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException ignored) {
        } finally {
            executorService.shutdownNow();
        }
    }

    @Override
    public void whenOfComplete(FailureConsumer<Object> result) {
        CountDownLatch latch = new CountDownLatch(taskList.size());
        for (ThreadTask task : taskList) {
            executorService.execute(() -> {
                try {
                    Object execute = task.execute(null);
                    result.accept(execute);
                } catch (Throwable e) {
                    result.failure(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException ignored) {
        } finally {
            executorService.shutdownNow();
        }
    }

    @Override
    public void anyOfComplete() {
        unitOfComplete(1);
    }

    @Override
    public void unitOfComplete(int unit) {
        unitOfComplete(unit, null);
    }

    @Override
    public void unitOfComplete(int unit, Consumer<List<Object>> consumer) {
        CountDownLatch latch = new CountDownLatch(unit);
        List<Object> result = new LinkedList<>();
        for (ThreadTask task : taskList) {
            executorService.execute(() -> {
                synchronized (this) {
                    if (latch.getCount() < 1) {
                        return;
                    }
                    try {
                        result.add(task.execute(null));
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException ignored) {
        } finally {
            if (null != consumer) {
                consumer.accept(result);
            }
            executorService.shutdownNow();
        }
    }

    @Override
    public void allOfComplete(FailureConsumer<List<Object>> consumer) {
        CountDownLatch latch = new CountDownLatch(taskList.size());
        List<Object> result = new LinkedList<>();
        for (ThreadTask task : taskList) {
            executorService.execute(() -> {
                try {
                    result.add(task.execute(null));
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException ignored) {
            consumer.failure(ignored);
        } finally {
            consumer.accept(result);
            executorService.shutdownNow();
        }
    }

    @Override
    public void anyOfComplete(Consumer<List<Object>> consumer) {
        unitOfComplete(1, consumer);
    }

    @Override
    public void run() {
        for (ThreadTask task : taskList) {
            executorService.execute(() -> {
                synchronized (this) {
                    task.execute(null);
                }
            });
        }
    }

    @Override
    public void run(Runnable runnable) {
        executorService.execute(runnable);
    }

    @Override
    public void run(Callable callable) {
        executorService.submit(callable);
    }


    @Override
    public void close() throws Exception {
        executorService.shutdownNow();
    }
}
