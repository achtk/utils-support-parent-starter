package com.chua.common.support.reflection;

/**
 * 反射
 *
 * @author CH
 */
public class Reflect<T> extends AbstractReflect<T> {


    public Reflect(Class<T> type) {
        super(type);
    }

    public Reflect(T entity, Class<T> type) {
        super(entity, type);
    }

    public static <T> Reflect<T> create(Class<T> type) {
        return new Reflect<>(type);
    }

    public static <T> Reflect<T> create(T entity, Class<T> type) {
        return new Reflect<>(entity, type);
    }

    public static <T> Reflect<T> create(T entity) {
        return new Reflect<>(entity, null == entity ? null : (Class<T>) entity.getClass());
    }


}
