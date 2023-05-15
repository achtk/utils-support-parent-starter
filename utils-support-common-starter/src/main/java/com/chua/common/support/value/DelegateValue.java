package com.chua.common.support.value;

import lombok.AllArgsConstructor;

/**
 * 空值
 *
 * @author CH
 */
@AllArgsConstructor
public  class DelegateValue<T> implements Value<T> {
    public final T object;
    @Override
    public T getValue() {
        return object;
    }

    @Override
    public boolean isNull() {
        return null != object;
    }
}
