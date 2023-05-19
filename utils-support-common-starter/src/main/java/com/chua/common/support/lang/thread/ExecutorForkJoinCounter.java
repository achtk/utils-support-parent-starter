package com.chua.common.support.lang.thread;


import com.chua.common.support.function.FailureConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * ForkJoinPool
 *
 * @author CH
 * @version 1.0.0
 * @see ForkJoinPool
 * @see ExecutorCounter
 * @see ThreadPoolExecutorCounter
 * @since 2021/5/21
 */
@Slf4j
public class ExecutorForkJoinCounter extends ForkJoinPool implements ExecutorCounter {

    private final List<?> source;
    private final List<ThreadTask> tasks = new LinkedList<>();
    /**
     * 临界值
     */
    private final long critical;

    public ExecutorForkJoinCounter(List<?> source, long critical) {
        this.critical = critical < 0 ? 10000L : critical;
        this.source = source;
    }

    @Override
    public ExecutorService executorService() {
        return null;
    }

    @Override
    public ExecutorCounter combine(ThreadTask runnable) {
        this.tasks.add(runnable);
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
        this.invoke(new ForkJoinWork(source));
        this.shutdownNow();
    }

    @Override
    public void whenOfComplete(FailureConsumer<Object> result) {
        this.invoke(new ForkJoinWork(source, result));
        this.shutdownNow();
    }

    @Override
    public void anyOfComplete() {
        throw new UnsupportedOperationException("不支持该操作");
    }

    @Override
    public void allOfComplete(FailureConsumer<List<Object>> consumer) {
        List<Object> result = new LinkedList<>();
        whenOfComplete(new FailureConsumer<Object>() {
            @Override
            public void failure(Throwable e) {
            }

            @Override
            public void accept(Object o) {
                result.add(o);
            }
        });
        consumer.accept(result);
    }

    @Override
    public void unitOfComplete(int unit) {
        throw new UnsupportedOperationException("不支持该操作");
    }

    @Override
    public void unitOfComplete(int unit, Consumer<List<Object>> consumer) {
        throw new UnsupportedOperationException("不支持该操作");
    }

    @Override
    public void anyOfComplete(Consumer<List<Object>> consumer) {
        throw new UnsupportedOperationException("不支持该操作");
    }

    @Override
    public void run() {
        allOfComplete();
    }

    @Override
    public void run(Runnable runnable) {
        combine(runnable);
        allOfComplete();
    }

    @Override
    public void run(Callable callable) {
        combine(callable);
        allOfComplete();
    }

    final class ForkJoinWork extends RecursiveTask<Object> {
        private final List<?> source;
        private final int start;
        private final int end;
        private FailureConsumer<Object> result;

        public ForkJoinWork(List<?> source) {
            this(source, 0, source.size());
        }

        public ForkJoinWork(List<?> source, int start, int end) {
            this.source = source;
            this.start = start;
            this.end = end;
        }

        public ForkJoinWork(List<?> source, int start, int end, FailureConsumer<Object> result) {
            this.source = source;
            this.start = start;
            this.end = end;
            this.result = result;
        }

        public ForkJoinWork(List<?> source, FailureConsumer<Object> result) {
            this(source);
            this.result = result;
        }

        @Override
        protected Object compute() {
            // 该任务负责求和的部分的大小
            int length = end - start;
            // 如果大小小于或等于阈值，顺序计算结果
            if (length <= critical) {
                computeSequentially();
                return null;
            }

            ForkJoinWork leftTask = new ForkJoinWork(source, start, start + length / 2, result);
            // 利用另一个ForkJoinPool线程异步执行新创建的子任务
            leftTask.fork();
            ForkJoinWork rightTask = new ForkJoinWork(source, start + length / 2, end, result);
            // 利用另一个ForkJoinPool线程异步执行新创建的子任务
            rightTask.fork();
            // 同步执行第二个子任务，有可能允许进一步递归划分
            leftTask.join();
            // 同步执行第二个子任务，有可能允许进一步递归划分
            rightTask.join();
            return null;
        }

        /**
         * 求和
         */
        private void computeSequentially() {
            for (int i = start; i < end; i++) {
                notifyTask(i);
            }
        }

        /**
         * 通知任务
         *
         * @param i 索引
         */
        private void notifyTask(int i) {
            tasks.forEach(it -> {
                Object execute;
                execute = it.execute(source.get(i));
                if (null != result) {
                    result.accept(execute);
                }
            });
        }
    }
}