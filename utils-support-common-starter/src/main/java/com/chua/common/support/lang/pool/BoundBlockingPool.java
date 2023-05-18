package com.chua.common.support.lang.pool;

import com.chua.common.support.function.SafeConsumer;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * 绑定对象池
 *
 * @author CH
 */
public class BoundBlockingPool<T> extends GenericObjectPool<T> implements Pool<T> {

    public BoundBlockingPool(ObjectFactory<T> objectFactory) {
        super(new PoolPooledObjectFactory<>(objectFactory));
    }

    public BoundBlockingPool(ObjectFactory<T> objectFactory, SafeConsumer<T> consumer) {
        super(new PoolPooledObjectFactory<>(objectFactory, consumer));
    }

    public BoundBlockingPool(ObjectFactory<T> objectFactory, PoolConfig poolConfig) {
        super(new PoolPooledObjectFactory<>(objectFactory), poolConfig);
    }

    public BoundBlockingPool(ObjectFactory<T> objectFactory, PoolConfig poolConfig, SafeConsumer<T> consumer) {
        super(new PoolPooledObjectFactory<>(objectFactory, consumer), poolConfig);
    }


    @Override
    public String toString() {
        return "BoundBlockingPool{}";
    }
}
