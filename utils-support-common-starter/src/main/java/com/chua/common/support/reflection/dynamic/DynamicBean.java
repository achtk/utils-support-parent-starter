package com.chua.common.support.reflection.dynamic;

/**
 * 动态对象
 *
 * @author ch
 */
public interface DynamicBean {

    /**
     * 构建对象
     *
     * @param type 類型
     * @return 对象
     */
    <T> T createBean(Class<T> type);

    /**
     * 构建对象
     *
     * @param type 類型
     * @return 对象
     */
    <T> Class<T> createType(Class<T> type);

}
