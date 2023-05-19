package com.chua.redis.support.lock;

import com.chua.common.support.lang.lock.Lock;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * redis鎖
 *
 * @author CH
 * @since 2022-05-27
 */
@Slf4j
public class RedisLock implements Lock {
    final RedissonClient redisson;
    private final String lockName;
    private final RLock lock;

    public RedisLock(String lockName, String address, int database, String password) {
        this.lockName = lockName;
        Config config = new Config();
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(database)
                .setPassword(password);
        this.redisson = Redisson.create(config);
        this.lock = redisson.getLock(lockName);
    }


    @Override
    public boolean lock(int timeout) {
        try {
            return lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        return false;
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public boolean tryLock() {
        return lock();
    }

    @Override
    public String toString() {
        return lockName;
    }
}
