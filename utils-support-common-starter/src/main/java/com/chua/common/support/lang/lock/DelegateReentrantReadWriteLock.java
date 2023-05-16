package com.chua.common.support.lang.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁
 *
 * @author CH
 */
public class DelegateReentrantReadWriteLock implements ReadWriteLock {

    final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    @Override
    public Lock readLock() {
        ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
        if (null == readLock) {
            return null;
        }
        return new DelagateReadLock(readLock);
    }

    @Override
    public Lock writeLock() {
        ReentrantReadWriteLock.WriteLock writeLock = null;
        try {
            writeLock = reentrantReadWriteLock.writeLock();
        } catch (Exception e) {
            return null;
        }
        if (null == writeLock) {
            return null;
        }

        return new DelagateWriteLock(writeLock);
    }

    final class DelagateWriteLock implements Lock {

        private ReentrantReadWriteLock.WriteLock writeLock;

        public DelagateWriteLock(ReentrantReadWriteLock.WriteLock readLock) {
            this.writeLock = readLock;
        }

        @Override
        public boolean lock(int timeout) {
            try {
                return writeLock.tryLock(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                return false;
            }
        }

        @Override
        public void unlock() {
            writeLock.unlock();
        }

        @Override
        public boolean tryLock() {
            return writeLock.tryLock();
        }
    }

    final class DelagateReadLock implements Lock {

        private ReentrantReadWriteLock.ReadLock readLock;

        public DelagateReadLock(ReentrantReadWriteLock.ReadLock readLock) {
            this.readLock = readLock;
        }

        @Override
        public boolean lock(int timeout) {
            try {
                return readLock.tryLock(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                return false;
            }
        }

        @Override
        public void unlock() {
            readLock.unlock();
        }

        @Override
        public boolean tryLock() {
            return readLock.tryLock();
        }
    }
}
