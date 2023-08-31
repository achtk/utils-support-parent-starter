package com.chua.common.support.value;

import com.chua.common.support.converter.Converter;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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
        if(value instanceof Throwable) {
            return ofThrowable((Throwable) value);
        }
        return null == value ? (Value<T>) NullValue.INSTANCE : new DelegateValue<>(value);
    }

    /**
     * 初始化
     *
     * @param throwable 数据
     * @param <T>       类型
     * @return value
     */
    @SuppressWarnings("ALL")
    static <T> Value<T> ofThrowable(Throwable throwable) {
        return null == throwable ? (Value<T>) NullValue.INSTANCE : (Value<T>) new ThrowableValue(throwable);
    }

    /**
     * 初始化
     *
     * @param value 数据
     * @param e     异常
     * @param <T>   类型
     * @return value
     */
    @SuppressWarnings("ALL")
    static <T> Value<T> of(T value, Throwable e) {
        return null == value ? (Value<T>) NullValue.INSTANCE : new DelegateValue<>(value, e);
    }

    /**
     * 初始化
     *
     * @param value        数据
     * @param defaultValue 默认值
     * @param <T>          类型
     * @return value
     */
    @SuppressWarnings("ALL")
    static <T> Value<T> of(T value, T defaultValue) {
        return null == value ? (Value<T>) NullValue.INSTANCE : new DelegateValue<>(value, defaultValue);
    }

    /**
     * 动态
     * 回调
     *
     * @param function 函数
     * @return 回调
     */
    static <R, T>Value<T> ofDynamic(Function<R, T> function) {
        return new DynamicValue<>(function);
    }

    /**
     * 地图动态
     * 回调
     *
     * @param function 函数
     * @return 回调
     */
    static <T>Value<T> ofMapDynamic(Function<Map<String, Object>, T> function) {
        return new MapDynamicValue<>(function);
    }
    /**
     * 获取值
     *
     * @return 值
     */
    T getValue();
    /**
     * 获取值
     * @param defaultValue 默认值
     * @return 值
     */
    @SuppressWarnings("ALL")
    default T getDefaultValue(Object defaultValue) {
        return Optional.ofNullable(getValue()).orElse((T) defaultValue);
    }

    /**
     * 获取值
     *
     * @param target 类型
     * @param <E>    类型
     * @return 值
     */
    @SuppressWarnings("ALL")
    default <E> E getValue(Class<E> target) {
        if (target == Object.class) {
            return (E) getValue();
        }
        return Converter.convertIfNecessary(getValue(), target);
    }

    /**
     * 获取异常
     *
     * @return 异常
     */
    Throwable getThrowable();

    /**
     * 是否为空
     *
     * @return 是否为空
     */
    boolean isNull();

    /**
     * 获取值
     * @return 值
     */
    default String getStringValue() {
        return getValue(String.class);
    }
}
