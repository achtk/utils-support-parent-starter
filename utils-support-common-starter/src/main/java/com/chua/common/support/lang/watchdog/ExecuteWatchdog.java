package com.chua.common.support.lang.watchdog;

import com.chua.common.support.spi.ServiceProvider;

import java.util.function.Consumer;

/**
 * 观察器
 *
 * @author CH
 */
public class ExecuteWatchdog implements TimeoutObserver {

    public static final long INFINITE_TIMEOUT = -1;
    private boolean killedProcess;
    private final Watchdog watchdog;
    private volatile boolean processStarted;
    private Exception caught;
    private final boolean hasWatchdog;
    private boolean watch;
    private Consumer closeConsumer;

    private Object process;
    private TimeoutHandler timeoutHandler = new TimeoutHandler.SimpleTimeoutHanlder();

    public ExecuteWatchdog(final long timeout) {
        this(timeout, null);
    }

    public ExecuteWatchdog(final long timeout, Consumer<Object> closeConsumer) {
        this.killedProcess = false;
        this.closeConsumer = closeConsumer;
        this.watch = false;
        this.hasWatchdog = timeout != INFINITE_TIMEOUT;
        this.processStarted = false;
        if (this.hasWatchdog) {
            this.watchdog = ServiceProvider.of(Watchdog.class).getSpiService();
            this.watchdog.timeout(timeout);
            this.watchdog.addTimeoutObserver(this);
        } else {
            this.watchdog = null;
        }
        this.setProcessNotStarted();
    }


    @Override
    public void timeoutOccured(Watchdog w) {
        try {
            try {
                // We must check if the process was not stopped
                // before being here
                if (process != null) {
                    timeoutHandler.destroy(closeConsumer);
                }
            } catch (final IllegalThreadStateException itse) {
                // the process is not terminated, if this is really
                // a timeout and not a manual stop then destroy it.
                if (watch) {
                    killedProcess = true;
                    timeoutHandler.destroyForce(closeConsumer);
                }
            }
        } catch (final Exception e) {
            caught = e;
        } finally {
            cleanUp();
        }
    }

    @Override
    public void setProcessNotStarted() {
        processStarted = false;
    }

    protected synchronized void cleanUp() {
        watch = false;
        process = null;
    }

    @Override
    public void handler(TimeoutHandler timeoutHandler) {
        this.timeoutHandler = timeoutHandler;
    }

    @Override
    public <T> void observer(Consumer<T> closeConsumer) {
        this.closeConsumer = closeConsumer;
    }

    @Override
    public void checkException() throws Exception {
        if (caught != null) {
            throw caught;
        }
    }

    @Override
    public synchronized void monitor(Object processToMonitor) {
        if (processToMonitor == null) {
            throw new NullPointerException("process is null.");
        }
        if (this.process != null) {
            throw new IllegalStateException("Already running.");
        }
        this.caught = null;
        this.killedProcess = false;
        this.watch = true;
        this.process = processToMonitor;
        this.processStarted = true;
        this.timeoutHandler.handler(processToMonitor);
        this.notifyAll();
        if (this.hasWatchdog) {
            watchdog.start(closeConsumer);
        }
    }

    @Override
    public synchronized void stop(Consumer<Object> consumer) {
        if (hasWatchdog) {
            watchdog.stop();
        }
        watch = false;
        process = null;
    }
}
