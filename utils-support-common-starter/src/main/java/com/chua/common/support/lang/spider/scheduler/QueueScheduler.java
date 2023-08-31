package com.chua.common.support.lang.spider.scheduler;

import com.chua.common.support.lang.spider.Request;
import com.chua.common.support.lang.spider.Site;
import com.chua.common.support.lang.spider.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Basic Scheduler implementation.<br>
 * Store urls to fetch in LinkedBlockingQueue and remove duplicate urls by HashMap.
 * <p>
 * Note: if you use this {@link QueueScheduler}
 * with {@link Site#getCycleRetryTimes()} enabled, you may encountered dead-lock
 * when the queue is full.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class QueueScheduler extends AbstractDuplicateRemovedScheduler implements MonitorableScheduler {

    private final BlockingQueue<Request> queue;

    public QueueScheduler() {
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Creates a {@code QueueScheduler} with the given (fixed) capacity.
     *
     * @param capacity the capacity of this queue,
     *                 see {@link LinkedBlockingQueue#LinkedBlockingQueue(int)}
     * @since 0.8.0
     */
    public QueueScheduler(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        logger.trace("Remaining capacity: {}", this.queue.remainingCapacity());

        try {
            queue.put(request);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Request poll(Task task) {
        return queue.poll();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return queue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
}
