package com.chua.common.support.lang.watchdog;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.ThreadUtils;

import java.util.Enumeration;
import java.util.Vector;
import java.util.function.Consumer;

/**
 * watchdog
 *
 * @author CH
 */
@Spi("simple")
public class SimpleWatchdog implements Watchdog, Runnable {

    private final Vector<TimeoutObserver> observers = new Vector<TimeoutObserver>(1);

    private long timeout;

    private boolean stopped = false;
    private Consumer<Object> consumer;

    public SimpleWatchdog() {
    }

    public SimpleWatchdog(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void run() {
        final long startTime = System.currentTimeMillis();
        boolean isWaiting;
        synchronized (this) {
            long timeLeft = timeout - (System.currentTimeMillis() - startTime);
            isWaiting = timeLeft > 0;
            while (!stopped && isWaiting) {
                try {
                    wait(timeLeft);
                } catch (final InterruptedException ignored) {
                }
                timeLeft = timeout - (System.currentTimeMillis() - startTime);
                isWaiting = timeLeft > 0;
            }
        }

        // notify the listeners outside of the synchronized block (see EXEC-60)
        if (!isWaiting) {
            fireTimeoutOccured();
        }
    }

    @Override
    public Watchdog timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public Watchdog addTimeoutObserver(TimeoutObserver to) {
        observers.addElement(to);
        return this;
    }

    @Override
    public Watchdog removeTimeoutObserver(TimeoutObserver to) {
        observers.removeElement(to);
        return this;
    }

    @Override
    public void fireTimeoutOccured() {
        final Enumeration<TimeoutObserver> e = observers.elements();
        while (e.hasMoreElements()) {
            e.nextElement().timeoutOccured(this);
        }
    }

    @Override
    public void start(Consumer<Object> consumer) {
        stopped = false;
        this.consumer = consumer;
        final Thread t = ThreadUtils.newThread(this);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void stop() {
        stopped = true;
        try {
            notifyAll();
        } catch (Exception ignored) {
        }
    }
}
