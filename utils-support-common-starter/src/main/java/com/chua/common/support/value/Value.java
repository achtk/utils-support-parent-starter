package com.chua.common.support.value;

import java.io.Serializable;

/**
 * 值
 *
 * @author CH
 */
public interface Value<T> extends Serializable {
    /**
     * 初始化
     *
     * @param value 数据
     * @param <T>   类型
     * @return value
     */
    @SuppressWarnings("ALL")
    static <T> Value<T> of(T value) {
        return null == value ? (Value<T>) NullValue.INSTANCE : new DelegateValue<>(value);
    }

    /**
     * 获取值
     *
     * @return 值
     */
    T getValue();

    /**
     * 是否为空
     *
     * @return 是否为空
     */
    boolean isNull();
}
