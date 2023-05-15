package com.chua.common.support.utils;

import com.chua.common.support.function.NamedThreadFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


/**
 * 线程工具
 *
 * @author CH
 */
public class ThreadUtils {

    /**
     *
     */
    private static final int SINGLETON = 1;
    /**
     *
     */
    private static final long KEEP_ALIVE_TIME = 0L;

    private static final int PROCESSOR = processor();
    private static final String DEFAULT = "default";

    private static final ExecutorService THREAD_POOL = newProcessorThreadExecutor("static-thread-pool");

    static {
        Runtime.getRuntime().addShutdownHook(newThread(THREAD_POOL::shutdownNow));
    }

    /**
     * 关闭线程池
     *
     * @param executor 处理器
     */
    public static void closeQuietly(final Executor executor) {
        if (executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdownNow();
        }
    }

    /**
     * 关闭对象
     *
     * @param closeable 处理器
     */
    public static void closeQuietly(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 关闭线程
     *
     * @param thread 线程
     */
    public static void closeQuietly(final Thread thread) {
        if (thread != null) {
            thread.interrupt();
        }
    }

    /**
     * 线程
     */
    public static void newAndRunThread(final Runnable runnable) {
        ExecutorService executorService = newSingleThreadExecutor();
        executorService.execute(runnable);
    }

    /**
     * 线程池
     *
     * @return 线程池
     */
    public static ExecutorService newCachedThreadPool() {
        return newCachedThreadPool(DEFAULT);
    }

    /**
     * 线程池
     *
     * @param name 线程池名称
     * @return 线程池
     */
    public static ExecutorService newCachedThreadPool(final String name) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), newThreadFactory(name));
    }

    /**
     * 线程池
     *
     * @param name     名称
     * @param supplier 消费者
     * @return 线程池
     */
    public static ExecutorService newCachedThreadPool(String name, Supplier<Runnable> supplier) {
        ExecutorService executorService = newCachedThreadPool(name);
        executorService.execute(supplier.get());
        return executorService;
    }

    /**
     * 线程池
     *
     * @param threadFactory 线程池
     * @return 线程池
     */
    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactory);
    }

    /**
     * 获取CompletedFuture
     *
     * @return CompletableFuture
     */
    public static <T> CompletableFuture<T> newCompletableFuture() {
        return new CompletableFuture<>();
    }

    /**
     * 初始化线程池
     *
     * @param threadFactory 线程工厂
     * @param executor      线程池
     * @return 线程池
     */
    public static Executor newExecutor(ThreadFactory threadFactory, Executor executor) {
        return newExecutor(threadFactory, executor, processor());
    }

    /**
     * 初始化线程池
     *
     * @param threadFactory 线程工厂
     * @param executor      线程池
     * @param size          数量
     * @return 线程池
     */
    public static Executor newExecutor(ThreadFactory threadFactory, Executor executor, int size) {
        return Optional.ofNullable(executor).orElse(newFixedThreadExecutor(size, threadFactory));
    }

    /**
     * 线程池
     *
     * @return ExecutorService
     */
    public static ExecutorService newFixedThreadExecutor(final int max) {
        return newFixedThreadExecutor(max, DEFAULT);
    }

    /**
     * 线程池
     *
     * @param thread 线程数
     * @param name   名称
     * @return 线程池
     */
    public static ExecutorService newFixedThreadExecutor(int thread, String name) {
        return Executors.newFixedThreadPool(thread, newThreadFactory(name));
    }

    /**
     * 线程池
     *
     * @param thread   线程数
     * @param name     名称
     * @param supplier 消费者
     * @return 线程池
     */
    public static ExecutorService newFixedThreadExecutor(int thread, String name, Supplier<Runnable> supplier) {
        ExecutorService executorService = newFixedThreadExecutor(thread, name);
        for (int i = 0; i < thread; i++) {
            executorService.execute(supplier.get());
        }

        return executorService;
    }

    /**
     * 线程池
     *
     * @param thread        线程数
     * @param threadFactory 线程池
     * @return 线程池
     */
    public static ExecutorService newFixedThreadExecutor(int thread, ThreadFactory threadFactory) {
        return Executors.newFixedThreadPool(thread, threadFactory);
    }

    /**
     * 线程池
     *
     * @return ExecutorService
     */
    public static ExecutorService newForkJoinPool() {
        return ForkJoinPool.commonPool();
    }

    /**
     * 线程池
     *
     * @return ExecutorService
     */
    public static ExecutorService newMaxThreadExecutor(final int value1, final int value2) {
        return newFixedThreadExecutor(Math.max(value1, value2));
    }

    /**
     * 线程池
     *
     * @return ExecutorService
     */
    public static ExecutorService newMaxThreadExecutor(final int value1, final int value2, final String name) {
        return newFixedThreadExecutor(Math.max(value1, value2), name);
    }

    /**
     * 线程池
     *
     * @return ExecutorService
     */
    public static ExecutorService newMinThreadExecutor(final int value1, final int value2) {
        return newFixedThreadExecutor(Math.min(value1, value2));
    }

    /**
     * 线程池
     *
     * @return ExecutorService
     */
    public static ExecutorService newMinThreadExecutor(final int value1, final int value2, final String name) {
        return newFixedThreadExecutor(Math.min(value1, value2), name);
    }

    /**
     * 线程池
     *
     * @return ExecutorService
     */
    public static ExecutorService newProcessorThreadExecutor() {
        return newFixedThreadExecutor(PROCESSOR);
    }

    /**
     * 线程池
     *
     * @return ExecutorService
     */
    public static ExecutorService newProcessorThreadExecutor(final int core) {
        return newFixedThreadExecutor(Math.min(core, PROCESSOR));
    }

    /**
     * 线程池
     *
     * @return ExecutorService
     */
    public static ExecutorService newProcessorThreadExecutor(final String name) {
        return newFixedThreadExecutor(PROCESSOR, name);
    }

    /**
     * 线程池
     *
     * @param thread 线程数
     * @return 线程池
     */
    public static ScheduledExecutorService newScheduledThreadPoolExecutor(final int thread) {
        return new ScheduledThreadPoolExecutor(thread, newThreadFactory(DEFAULT));
    }

    /**
     * 线程池
     *
     * @param initialDelay 初始化延迟
     * @param delay        延迟
     * @param runnable     线程
     * @param unit         时间
     * @return 线程池
     */
    public static ScheduledFuture newScheduleWithFixedDelay(final Runnable runnable, long initialDelay,
                                                            long delay,
                                                            TimeUnit unit) {
        return newScheduleWithFixedDelay(DEFAULT, runnable, initialDelay, delay, unit);
    }

    /**
     * 线程池
     *
     * @param threadName   线程名称
     * @param initialDelay 初始化延迟
     * @param delay        延迟
     * @param runnable     线程
     * @param unit         时间
     * @return 线程池
     */
    public static ScheduledFuture newScheduleWithFixedDelay(final String threadName, final Runnable runnable, long initialDelay,
                                                            long delay,
                                                            TimeUnit unit) {
        return newScheduledThreadPoolExecutor(1, newThreadFactory(threadName))
                .scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }

    /**
     * 线程池
     *
     * @param thread 线程数
     * @param name   线程池名称
     * @return 线程池
     */
    public static ScheduledExecutorService newScheduledThreadPoolExecutor(final int thread, final String name) {
        return new ScheduledThreadPoolExecutor(thread, newThreadFactory(name));
    }

    /**
     * 线程池
     *
     * @param thread        线程数
     * @param threadFactory 线程池
     * @return 线程池
     */
    public static ScheduledExecutorService newScheduledThreadPoolExecutor(final int thread, final ThreadFactory threadFactory) {
        return new ScheduledThreadPoolExecutor(thread, threadFactory);
    }

    /**
     * 线程池
     *
     * @return 线程池
     */
    public static ScheduledExecutorService newScheduledThreadPoolExecutor(final String name) {
        return new ScheduledThreadPoolExecutor(1, newThreadFactory(name));
    }

    /**
     * @return ExecutorService
     */
    public static ExecutorService newSingleThreadExecutor() {
        return newSingleThreadExecutor(DEFAULT);
    }

    /**
     * 单例线程池
     *
     * @param name 线程池名称
     * @return ExecutorService
     */
    public static ExecutorService newSingleThreadExecutor(String name) {
        return new ThreadPoolExecutor(
                SINGLETON,
                SINGLETON,
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                newThreadFactory(name)

        );
    }

    /**
     * 单例线程池
     *
     * @param threadFactory 线程池
     * @return ExecutorService
     */
    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(
                SINGLETON,
                SINGLETON,
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory
        );
    }

    /**
     * 线程
     *
     * @param runnable runnable
     * @return 线程
     * @see Runnable
     */
    public static Thread newThread(final Runnable runnable) {
        return new Thread(runnable);
    }

    /**
     * 线程
     *
     * @param runnable runnable
     * @param name     名称
     * @return 线程
     * @see Runnable
     */
    public static Thread newThread(final Runnable runnable, final String name) {
        Thread thread = newThread(runnable);
        thread.setName(name);

        return thread;
    }

    /**
     * 线程工厂
     *
     * @param name 名称
     * @return ThreadFactory
     */
    public static ThreadFactory newThreadFactory(final String name) {
        return newThreadFactory(name, 0);
    }

    /**
     * 线程工厂
     *
     * @param name 名称
     * @return ThreadFactory
     */
    public static ThreadFactory newThreadFactory(final String name, final int index) {
        return new DefaultThreadFactory(name, index);
    }

    /**
     * cpu数量
     *
     * @return cpu数量
     */
    public static int processor() {
        return Runtime.getRuntime().availableProcessors() * 2 - 1;
    }

    /**
     * 关闭线程池
     *
     * @param executor 处理器
     */
    public static void shutdownNow(final Executor executor) {
        if (executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdownNow();
        }
    }

    /**
     * 暂停
     *
     * @param time 时间
     */
    public static void sleep(long time) throws InterruptedException {
        Thread.sleep(time);
    }

    /**
     * 暂停
     *
     * @param time     时间
     * @param timeUnit 类型
     */
    public static void sleep(long time, TimeUnit timeUnit) throws InterruptedException {
        long unit = timeUnit.toMillis(time);
        Thread.sleep(unit);
    }

    /**
     * 暂停
     *
     * @param time 时间
     */
    public static void sleepMillisecondsQuietly(long time) {
        sleepQuietly(time, TimeUnit.MILLISECONDS);
    }

    /**
     * 暂停
     *
     * @param time     时间
     * @param timeUnit 类型
     */
    public static void sleepQuietly(long time, TimeUnit timeUnit) {
        if (time < 0L) {
            return;
        }
        try {
            long unit = timeUnit.toMillis(time);
            Thread.sleep(Math.min(unit, Long.MAX_VALUE));
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * 暂停
     *
     * @param time 时间
     */
    public static void sleepSecondsQuietly(long time) {
        sleepQuietly(time, TimeUnit.SECONDS);
    }

    /**
     * 静态线程池线程
     *
     * @return 执行器
     */
    public static ExecutorService newStaticThreadPool() {
        return THREAD_POOL;
    }

    /**
     * 添加钩子
     *
     * @param runnable 执行器
     */
    public static void addShutdownHook(Runnable runnable) {
        Thread thread = ThreadUtils.newThread(runnable);
        thread.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(thread);
    }

    /**
     * 调度线程池
     * @param threadFactory 名称
     * @return this
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory) {
        return new DelegatedScheduledExecutorService
                (new ScheduledThreadPoolExecutor(1, threadFactory));
    }
    /**
     * 调度线程池
     * @return this
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return newSingleThreadScheduledExecutor(new NamedThreadFactory("schedule"));
    }

    static class DelegatedScheduledExecutorService
            extends DelegatedExecutorService
            implements ScheduledExecutorService {
        private final ScheduledExecutorService e;
        DelegatedScheduledExecutorService(ScheduledExecutorService executor) {
            super(executor);
            e = executor;
        }
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return e.schedule(command, delay, unit);
        }
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return e.schedule(callable, delay, unit);
        }
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            return e.scheduleAtFixedRate(command, initialDelay, period, unit);
        }
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            return e.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    }


    static class DelegatedExecutorService extends AbstractExecutorService {
        private final ExecutorService e;
        DelegatedExecutorService(ExecutorService executor) { e = executor; }
        public void execute(Runnable command) { e.execute(command); }
        public void shutdown() { e.shutdown(); }
        public List<Runnable> shutdownNow() { return e.shutdownNow(); }
        public boolean isShutdown() { return e.isShutdown(); }
        public boolean isTerminated() { return e.isTerminated(); }
        public boolean awaitTermination(long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.awaitTermination(timeout, unit);
        }
        public Future<?> submit(Runnable task) {
            return e.submit(task);
        }
        public <T> Future<T> submit(Callable<T> task) {
            return e.submit(task);
        }
        public <T> Future<T> submit(Runnable task, T result) {
            return e.submit(task, result);
        }
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
                throws InterruptedException {
            return e.invokeAll(tasks);
        }
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                             long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.invokeAll(tasks, timeout, unit);
        }
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
                throws InterruptedException, ExecutionException {
            return e.invokeAny(tasks);
        }
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                               long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return e.invokeAny(tasks, timeout, unit);
        }
    }
    /**
     * 默认线程工厂
     */
    public static final class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        public DefaultThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        public DefaultThreadFactory(String name, int index) {
            POOL_NUMBER.set(index);
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = name + "-" + POOL_NUMBER.getAndIncrement() + "-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
