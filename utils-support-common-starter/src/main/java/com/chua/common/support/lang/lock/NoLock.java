package com.chua.common.support.lang.lock;

/**
 * 无锁
 *
 * @author CH
 * @since 2022-05-27
 */
public class NoLock implements Lock {

    public static final Lock LOCK = new NoLock();

    @Override
    public boolean lock(int timeout) {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public boolean tryLock() {
        return true;
    }

}
