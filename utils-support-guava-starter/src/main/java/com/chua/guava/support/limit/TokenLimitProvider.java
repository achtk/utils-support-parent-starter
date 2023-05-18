package com.chua.guava.support.limit;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.task.limit.LimiterProvider;
import com.google.common.util.concurrent.RateLimiter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 限流
 *
 * @author CH
 */
@Spi({"token", "limit"})
@NoArgsConstructor
public class TokenLimitProvider implements LimiterProvider {

    private static final ConcurrentHashMap<String, RateLimiter> RATE_LIMITER_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    private static final int DEFAULT_SIZE = 10;

    public TokenLimitProvider(double token) {
        newLimiter(token);
    }

    @Override
    public LimiterProvider newLimiter(String name, double size) {
        RATE_LIMITER_CONCURRENT_HASH_MAP.put(name, RateLimiter.create(size));
        return this;
    }

    @Override
    public LimiterProvider newLimiter(Map<String, Integer> config) {
        for (Map.Entry<String, Integer> entry : config.entrySet()) {
            RATE_LIMITER_CONCURRENT_HASH_MAP.put(entry.getKey(), RateLimiter.create(entry.getValue()));
        }
        return this;
    }

    @Override
    public synchronized boolean tryAcquire(String name) {
        RateLimiter rateLimiter = RATE_LIMITER_CONCURRENT_HASH_MAP.get(name);
        if (null == rateLimiter) {
            newLimiter(name, DEFAULT_SIZE);
            return tryAcquire(name);
        }
        return rateLimiter.tryAcquire();
    }

    @Override
    public synchronized boolean tryAcquire(String name, long time) {
        RateLimiter rateLimiter = RATE_LIMITER_CONCURRENT_HASH_MAP.get(name);
        if (null == rateLimiter) {
            newLimiter(name, DEFAULT_SIZE);
            return tryAcquire(name);
        }
        return rateLimiter.tryAcquire(time, TimeUnit.MICROSECONDS);
    }

    @Override
    public synchronized boolean tryAcquire(String name, long time, TimeUnit timeUnit) {
        RateLimiter rateLimiter = RATE_LIMITER_CONCURRENT_HASH_MAP.get(name);
        if (null == rateLimiter) {
            newLimiter(name, DEFAULT_SIZE);
            return tryAcquire(name);
        }
        return rateLimiter.tryAcquire(time, timeUnit);
    }

    @Override
    public synchronized boolean tryGet() {
        return false;
    }

    @Override
    public synchronized boolean containGroup(String group) {
        return RATE_LIMITER_CONCURRENT_HASH_MAP.containsKey(group);
    }
}
