package com.chua.common.support.lang.pool;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.UsageTracking;
import org.apache.commons.pool2.impl.GenericObjectPoolMXBean;

/**
 * 池
 *
 * @author CH
 * @since 2022-05-23
 */
public interface Pool<T> extends ObjectPool<T>, GenericObjectPoolMXBean, UsageTracking<T>, AutoCloseable {

    /**
     * 获取对象
     *
     * @return 对象
     */
    default T getObject() {
        T borrowObject = null;
        try {
            borrowObject = borrowObject();
        } catch (Exception e) {
            return null;
        }

        if (borrowObject instanceof PoolAutoClose) {
            ((PoolAutoClose<T>) borrowObject).setDatasource(this);
        }
        return borrowObject;
    }

    /**
     * 释放对象
     *
     * @param obj 对象
     */
    default void releaseObject(T obj) {
        if (null == obj) {
            return;
        }

        try {
            returnObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
