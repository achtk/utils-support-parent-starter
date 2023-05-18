package com.chua.common.support.lang.pool;

import com.chua.common.support.reflection.Reflect;

/**
 * 反射对象工厂
 *
 * @author CH
 */
public class ReflectObjectFactory<T> implements ObjectFactory<T> {

    private Class<T> type;

    public ReflectObjectFactory(Class<T> type) {
        this.type = type;
    }

    @Override
    public T makeObject() {
        return Reflect.create(type).getObjectValue().getValue();
    }
}
