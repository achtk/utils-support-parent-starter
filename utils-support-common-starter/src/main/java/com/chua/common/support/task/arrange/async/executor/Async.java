package com.chua.common.support.task.arrange.async.executor;

import com.chua.common.support.task.arrange.Worker;
import com.chua.common.support.task.arrange.async.callback.DefaultGroupCallback;
import com.chua.common.support.task.arrange.async.callback.IGroupCallback;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 类入口，可以根据自己情况调整core线程的数量
 *
 * @author wuweifeng wrote on 2019-12-18
 * @version 1.0
 */
public class Async {
    /**
     * 默认不定长线程池
     */
    private static final ThreadPoolExecutor COMMON_POOL = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    /**
     * 注意，这里是个static，也就是只能有一个线程池。用户自定义线程池时，也只能定义一个
     */
    private static ExecutorService executorService;

    /**
     * 出发点
     */
    public static boolean beginWork(long timeout, ExecutorService executorService, List<Worker> workers) throws ExecutionException, InterruptedException {
        if (workers == null || workers.size() == 0) {
            return false;
        }
        //保存线程池变量
        Async.executorService = executorService;
        //定义一个map，存放所有的wrapper，key为wrapper的唯一id，value是该wrapper，可以从value中获取wrapper的result
        Map<String, Worker> forParamUseWrappers = new ConcurrentHashMap<>();
        CompletableFuture[] futures = new CompletableFuture[workers.size()];
        for (int i = 0; i < workers.size(); i++) {
            Worker wrapper = workers.get(i);
            futures[i] = CompletableFuture.runAsync(() -> wrapper.work(executorService, timeout, forParamUseWrappers), executorService);
        }
        try {
            CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
            return true;
        } catch (TimeoutException e) {
            Set<Worker> set = new HashSet<>();
            totalWorkers(workers, set);
            for (Worker wrapper : set) {
                wrapper.stopNow();
            }
            return false;
        }
    }

    /**
     * 如果想自定义线程池，请传pool。不自定义的话，就走默认的COMMON_POOL
     */
    public static boolean beginWork(long timeout, ExecutorService executorService, Worker... worker) throws ExecutionException, InterruptedException {
        if (worker == null || worker.length == 0) {
            return false;
        }
        List<Worker> workers = Arrays.stream(worker).collect(Collectors.toList());
        return beginWork(timeout, executorService, workers);
    }

    /**
     * 同步阻塞,直到所有都完成,或失败
     */
    public static boolean beginWork(long timeout, Worker... worker) throws ExecutionException, InterruptedException {
        return beginWork(timeout, COMMON_POOL, worker);
    }

    public static void beginWorkAsync(long timeout, IGroupCallback groupCallback, Worker... worker) {
        beginWorkAsync(timeout, COMMON_POOL, groupCallback, worker);
    }

    /**
     * 异步执行,直到所有都完成,或失败后，发起回调
     */
    public static void beginWorkAsync(long timeout, ExecutorService executorService, IGroupCallback groupCallback, Worker... worker) {
        if (groupCallback == null) {
            groupCallback = new DefaultGroupCallback();
        }
        IGroupCallback finalGroupCallback = groupCallback;
        if (executorService != null) {
            executorService.submit(() -> {
                try {
                    boolean success = beginWork(timeout, executorService, worker);
                    if (success) {
                        finalGroupCallback.success(Arrays.asList(worker));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(worker), new TimeoutException());
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    finalGroupCallback.failure(Arrays.asList(worker), e);
                }
            });
        } else {
            COMMON_POOL.submit(() -> {
                try {
                    boolean success = beginWork(timeout, COMMON_POOL, worker);
                    if (success) {
                        finalGroupCallback.success(Arrays.asList(worker));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(worker), new TimeoutException());
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    finalGroupCallback.failure(Arrays.asList(worker), e);
                }
            });
        }

    }

    /**
     * 总共多少个执行单元
     */
    @SuppressWarnings("unchecked")
    private static void totalWorkers(List<Worker> workers, Set<Worker> set) {
        set.addAll(workers);
        for (Worker wrapper : workers) {
            if (wrapper.getNextWorker() == null) {
                continue;
            }
            List<Worker> wrappers = wrapper.getNextWorker();
            totalWorkers(wrappers, set);
        }

    }

    /**
     * 关闭线程池
     */
    public static void shutDown() {
        shutDown(executorService);
    }

    /**
     * 关闭线程池
     */
    public static void shutDown(ExecutorService executorService) {
        if (executorService != null) {
            executorService.shutdown();
        } else {
            COMMON_POOL.shutdown();
        }
    }

    public static String getThreadCount() {
        return "activeCount=" + COMMON_POOL.getActiveCount() +
                "  completedCount " + COMMON_POOL.getCompletedTaskCount() +
                "  largestCount " + COMMON_POOL.getLargestPoolSize();
    }
}
