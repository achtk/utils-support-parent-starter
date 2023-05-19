package com.chua.common.support.lang.thread;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 线程处理器
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/14
 */
public class ThreadProvider {

    private static final String DEFAULT_NAME = "default-pool-executors";

    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    private static final int PROCESSOR = Runtime.getRuntime().availableProcessors();
    /**
     * 线程名称
     */
    private final String name;
    /**
     * 核心线程数，当线程池中的线程数量为 corePoolSize 时，即使这些线程处于空闲状态，也不会销毁（除非设置 allowCoreThreadTimeOut）。
     */
    private int corePoolSize = 1;
    /**
     * 最大线程数，线程池中允许的线程数量的最大值。
     */
    private int maximumPoolSize = 1;
    /**
     * 线程空闲时间，当线程池中的线程数大于 corePoolSize 时，多余的空闲线程将在销毁之前等待新任务的最长时间
     */
    private long keepAliveTime = 0L;
    /**
     * 线程空闲时间的单位。
     */
    private TimeUnit unit = TimeUnit.MILLISECONDS;
    /**
     * 任务队列
     */
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    /**
     * 线程工厂，线程池创建线程时使用的工厂。
     */
    private ThreadFactory threadFactory;
    /**
     * 拒绝策略，因达到线程边界和任务队列满时，针对新任务的处理方法。
     */
    private RejectedExecutionHandler rejectedHandler = new ThreadPoolExecutor.AbortPolicy();

    private ThreadProvider() {
        this(DEFAULT_NAME);
    }

    private ThreadProvider(String name) {
        this.name = Optional.ofNullable(name).orElse(DEFAULT_NAME);

        SecurityManager s = System.getSecurityManager();
        ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        String namePrefix = name + "-";

        this.threadFactory = r -> {
            Thread t = new Thread(group, r, namePrefix + THREAD_NUMBER.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        };
    }

    /**
     * 核心线程数
     *
     * @param coreSize 核心线程数
     * @return this
     */
    public ThreadProvider coreSize(int coreSize) {
        this.corePoolSize = coreSize;
        return this;
    }

    /**
     * 存活时间
     *
     * @param keepAliveTime 存活时间
     * @return this
     */
    public ThreadProvider keepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * 存活时间
     *
     * @param keepAliveTime 存活时间
     * @param timeUnit      时间单位
     * @return this
     */
    public ThreadProvider keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
        this.keepAliveTime = keepAliveTime;
        this.unit = timeUnit;
        return this;
    }

    /**
     * 最大线程数
     *
     * @param maximumPoolSize 最大线程数
     * @return this
     */
    public ThreadProvider maxSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    /**
     * 无限线程池
     *
     * @return 线程池
     */
    public ExecutorCounter newCachedThreadPool() {
        return this.coreSize(0).maxSize(Integer.MAX_VALUE).keepAliveTime(60L, SECONDS).workQueue(new SynchronousQueue<>()).newThreadPool();
    }

    /**
     * CPU 密集型
     *
     * @return 线程池
     */
    public ExecutorCounter newCpuIntensiveExecutor() {
        return newFixedThreadExecutor(PROCESSOR * 2);
    }


    /**
     * 初始化固定线程数线程池
     *
     * @return 线程池
     */
    public ExecutorCounter newFixedThreadExecutor() {
        return this.coreSize(corePoolSize)
                .maxSize(corePoolSize)
                .keepAliveTime(0, SECONDS).newThreadPool();
    }

    /**
     * 初始化固定线程数线程池
     *
     * @param thread 线程数
     * @return 线程池
     */
    public ExecutorCounter newFixedThreadExecutor(int thread) {
        return this.coreSize(thread).newFixedThreadExecutor();
    }


    /**
     * 初始化固定线程数线程池
     *
     * @param critical 临界值
     * @return 线程池
     */
    public <T> ExecutorCounter newForkJoinPool(List<T> source, long critical) {
        return new ExecutorForkJoinCounter(source, critical);
    }

    /**
     * IO密集型
     *
     * @param blockingFactor 阻塞系数
     * @return 线程池
     */
    public ExecutorCounter newIoIntensiveExecutor(float blockingFactor) {
        return newFixedThreadExecutor(Double.valueOf(PROCESSOR / (1 - blockingFactor)).intValue());
    }

    /**
     * 最大线程池
     *
     * @param values 值
     * @return 线程池
     */
    public ExecutorCounter newMaxThreadExecutor(int... values) {
        int maxValue = 0;
        for (int value : values) {
            maxValue = Math.max(maxValue, value);
        }
        return newFixedThreadExecutor(maxValue);
    }

    /**
     * 最小线程池
     *
     * @param values 值
     * @return 线程池
     */
    public ExecutorCounter newMinThreadExecutor(int... values) {
        int minValue = 0;
        for (int value : values) {
            minValue = Math.min(minValue, value);
        }
        return newFixedThreadExecutor(minValue);
    }

    /**
     * 调度线程池
     *
     * @return 线程池
     */
    public ScheduledThreadPoolExecutor newScheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
    }

    /**
     * 调度线程池
     *
     * @return 线程池
     */
    public ScheduledThreadPoolExecutor newScheduledThreadPoolExecutor(int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
    }

    /**
     * 单例线程池
     *
     * @return 线程池
     */
    public ExecutorCounter newSingleThreadExecutor() {
        return this.coreSize(1).maxSize(1).keepAliveTime(0L).newThreadPool();
    }

    /**
     * 初始化固定线程数线程池
     *
     * @return 线程池
     */
    public ExecutorCounter newWorkStealingPool() {
        return new ThreadPoolExecutorCounter(Executors.newWorkStealingPool());
    }

    /**
     * 初始化固定线程数线程池
     *
     * @param parallelism 目标并行度
     * @return 线程池
     * @throws IllegalArgumentException if {@code parallelism <= 0}
     */
    public ExecutorCounter newWorkStealingPool(int parallelism) {
        return new ThreadPoolExecutorCounter(Executors.newWorkStealingPool(parallelism));
    }

    /**
     * 线程工厂
     *
     * @param rejectedHandler 拒绝处理
     * @return this
     */
    public ThreadProvider rejectedHandler(RejectedExecutionHandler rejectedHandler) {
        this.rejectedHandler = rejectedHandler;
        return this;
    }

    /**
     * 线程工厂
     *
     * @param threadFactory 线程工厂
     * @return this
     */
    public ThreadProvider threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * 存活时间
     *
     * @param timeUnit 时间单位
     * @return this
     */
    public ThreadProvider timeUnit(TimeUnit timeUnit) {
        this.unit = timeUnit;
        return this;
    }

    /**
     * 队列
     *
     * @param workQueue 队列
     * @return this
     */
    public ThreadProvider workQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    /**
     * 初始化
     *
     * @return this
     */
    public static ThreadProvider empty() {
        return new ThreadProvider();
    }

    /**
     * 初始化
     *
     * @param name 线程名称
     * @return this
     */
    public static ThreadProvider of(String name) {
        return new ThreadProvider(name);
    }

    /**
     * 构建线程池
     *
     * @return 线程池
     */
    private ExecutorCounter newThreadPool() {
        return new ThreadPoolExecutorCounter(new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                rejectedHandler));
    }

//    /**
//     * 构建线程池
//     *
//     * @return 线程池
//     */
//    private ExecutorCounter newYieldThreadPool() {
//        return new ThreadYieldExecutorCounter();
//    }
}
