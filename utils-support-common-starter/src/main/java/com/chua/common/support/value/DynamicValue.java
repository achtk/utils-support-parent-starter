package com.chua.common.support.value;

import lombok.Getter;

import java.util.function.Function;

/**
 * 回调值
 * @param <T> 类型
 * @author CH
 */
public class DynamicValue<R, T> implements Value<T>{

    @Getter
    private Function<R, T> function;

    public DynamicValue(Function<R, T> function) {
        this.function = function;
    }

    @Override
    public T getValue() {
        return null;
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }

    @Override
    public boolean isNull() {
        return false;
    }
}
