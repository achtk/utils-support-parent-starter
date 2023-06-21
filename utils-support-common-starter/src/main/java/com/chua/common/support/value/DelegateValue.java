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
    private T value;

    private Throwable e;

    private T defaultValue;

    public DelegateValue(T value, T defaultValue) {
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public DelegateValue(T value, Throwable e) {
        this.value = value;
        this.e = e;
    }

    public DelegateValue(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        if(value instanceof Value) {
            value = (T) ((Value<?>) value).getValue();
        }
        return ObjectUtils.defaultIfNull(value, defaultValue);
    }

    @Override
    public Throwable getThrowable() {
        return e;
    }

    @Override
    public boolean isNull() {
        return null == value;
    }
}
