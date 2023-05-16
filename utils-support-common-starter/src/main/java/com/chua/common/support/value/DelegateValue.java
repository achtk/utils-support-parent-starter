package com.chua.common.support.value;

import com.chua.common.support.utils.ObjectUtils;
import lombok.AllArgsConstructor;

/**
 * 空值
 *
 * @author CH
 */
@AllArgsConstructor
public  class DelegateValue<T> implements Value<T> {
    private final T object;

    private Throwable e;

    private T defaultValue;

    public DelegateValue(T object, T defaultValue) {
        this.object = object;
        this.defaultValue = defaultValue;
    }

    public DelegateValue(T object, Throwable e) {
        this.object = object;
        this.e = e;
    }

    public DelegateValue(T object) {
        this.object = object;
    }

    @Override
    public T getValue() {
        return ObjectUtils.defaultIfNull(object, defaultValue);
    }

    @Override
    public Throwable getThrowable() {
        return e;
    }

    @Override
    public boolean isNull() {
        return null != object;
    }
}
