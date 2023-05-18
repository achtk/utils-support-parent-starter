package com.chua.common.support.lang.pool;

/**
 * 池化对象关闭
 *
 * @author CH
 */
public interface PoolAutoClose<T> extends AutoCloseable {
    /**
     * 数据库
     *
     * @param pool 池
     * @return 数据库
     */
    PoolAutoClose<T> setDatasource(Pool<T> pool);

    /**
     * 关闭
     */
    @Override
    void close();
}
