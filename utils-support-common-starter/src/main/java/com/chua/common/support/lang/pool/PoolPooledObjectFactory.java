package com.chua.common.support.lang.pool;

import com.chua.common.support.function.SafeConsumer;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.function.Consumer;

/**
 * 池化工厂
 *
 * @author CH
 */
public class PoolPooledObjectFactory<T> implements PoolFactory<T> {

    private ObjectFactory<T> objectFactory;
    private Consumer<T> consumer;

    public PoolPooledObjectFactory(ObjectFactory<T> objectFactory) {
        this.objectFactory = objectFactory;
    }

    public PoolPooledObjectFactory(ObjectFactory<T> objectFactory, SafeConsumer<T> consumer) {
        this.objectFactory = objectFactory;
        this.consumer = consumer;
    }

    @Override
    public void activateObject(PooledObject<T> p) throws Exception {

    }

    @Override
    public void destroyObject(PooledObject<T> p) throws Exception {
        p.deallocate();
        if (null != consumer) {
            consumer.accept(p.getObject());
        }
    }

    @Override
    public PooledObject<T> makeObject() throws Exception {
        return new DefaultPooledObject<>(objectFactory.makeObject());
    }

    @Override
    public void passivateObject(PooledObject<T> p) throws Exception {

    }

    @Override
    public boolean validateObject(PooledObject<T> p) {
        return objectFactory.validateObject(p.getObject());
    }
}
