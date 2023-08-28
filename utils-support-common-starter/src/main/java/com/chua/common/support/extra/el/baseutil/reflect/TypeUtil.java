package com.chua.common.support.extra.el.baseutil.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 基础类
 *
 * @author CH
 */
public abstract class TypeUtil<T> {
    private Type type;

    public TypeUtil() {
        ParameterizedType tmp = (ParameterizedType) (this.getClass().getGenericSuperclass());
        type = tmp.getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
