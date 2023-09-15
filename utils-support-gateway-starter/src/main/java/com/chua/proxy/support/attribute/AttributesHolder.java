package com.chua.proxy.support.attribute;

import java.util.Map;
import java.util.Objects;

/**
 * 属性持有者
 *
 * @author CH
 */
public interface AttributesHolder {

    /**
     * 获取属性
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getAttributes();

    /**
     * get属性
     *
     * @param name 名称
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    default <T> T getAttribute(String name) {
        return (T) getAttributes().get(name);
    }

    /**
     * 获取必需属性
     *
     * @param name 名称
     * @return {@link T}
     */
    default <T> T getRequiredAttribute(String name) {
        T value = getAttribute(name);
        Objects.requireNonNull(value, "Required attribute '" + name + "' is missing");
        return value;
    }

    /**
     * 获取属性或默认值
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    default <T> T getAttributeOrDefault(String name, T defaultValue) {
        return (T) getAttributes().getOrDefault(name, defaultValue);
    }

}
