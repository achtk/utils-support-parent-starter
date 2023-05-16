package com.chua.common.support.lang.lock;

/**
 * 锁
 *
 * @author CH
 * @since 2022-05-27
 */
public interface Lock extends AutoCloseable {
    /**
     * 加锁
     *
     * @return 加锁成功
     */
    default boolean lock() {
        return lock(-1);
    }

    /**
     * 超时锁
     *
     * @param timeout 超时时间
     * @return 加锁成功
     */
    boolean lock(int timeout);

    /**
     * 解锁
     */
    void unlock();

    /**
     * 尝试获取锁
     *
     * @return 尝试获取锁
     */
    boolean tryLock();

    /**
     * 关闭锁
     *
     * @throws Exception ex
     */
    @Override
    default void close() throws Exception {
        unlock();
    }
}
