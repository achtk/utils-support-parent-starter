package com.chua.common.support.context.definition;

import com.chua.common.support.context.enums.Scope;
import com.chua.common.support.utils.ClassUtils;

/**
 * 对象
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class ObjectDefinition<T> extends ClassDefinition<T> {

    public ObjectDefinition(T obj) {
        super(null == obj ? (Class<T>) Void.class : (Class<T>) ClassUtils.toType(obj));
        this.isLoaded = true;
        this.scope = Scope.SINGLE;
        this.object = obj;
    }


    public static <T>TypeDefinition<T> of(T bean) {
        return new ObjectDefinition<>(bean);
    }

    /**
     * 创建对象
     *
     * @param obj 对象
     * @return
     */
    public ObjectDefinition<T> setObject(T obj) {
        return new ObjectDefinition<>(obj);
    }


    public ObjectDefinition<T> addType(Class<?> type) {
        this.addInterfaceType(type);
        return this;
    }
}
