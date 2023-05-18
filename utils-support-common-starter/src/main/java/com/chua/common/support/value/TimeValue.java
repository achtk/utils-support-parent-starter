package com.chua.common.support.value;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * 携带时间值
 *
 * @author CH
 */
public class TimeValue<T> extends DelegateValue<T> {


    private final long entryTime = System.currentTimeMillis();

    private Duration timeout = Duration.ofMinutes(1);

    public TimeValue(T entity, Duration timeout) {
        super(entity);
        this.timeout = timeout;
    }

    /**
     * 初始化
     *
     * @param value   值
     * @param timeout 超时时间
     * @param <T>     类型
     * @return 结果
     */
    public static <T> TimeValue<T> of(T value, Duration timeout) {
        return new TimeValue<>(value, timeout);
    }

    /**
     * 初始化
     *
     * @param value 值
     * @param <T>   类型
     * @return 结果
     */
    public static <T> TimeValue<T> of(T value) {
        return new TimeValue<>(value);
    }


    @Override
    public T getValue() {
        if (isTimeout()) {
            return null;
        }
        return super.getValue();
    }

    @Override
    public Throwable getThrowable() {
        return isTimeout() ? new TimeoutException() : null;
    }

    /**
     * 获取生成时间
     *
     * @return 生成时间
     */
    public long getEntryTime() {
        return entryTime;
    }

    /**
     * 获取生成时间
     *
     * @return 生成时间
     */
    public boolean isTimeout() {
        return (entryTime + timeout.toMillis()) < System.currentTimeMillis();
    }
}
