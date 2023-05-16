package com.chua.common.support.lang.lock;

/**
 * 读写锁
 *
 * @author CH
 */
public interface ReadWriteLock {

    /**
     * 读锁
     *
     * @return 读锁
     */
    Lock readLock();

    /**
     * 写锁
     *
     * @return 写锁
     */
    Lock writeLock();
}
