package com.chua.common.support.lang.pool;

/**
 * 对象生成器
 *
 * @author CH
 */
public interface ObjectFactory<T> {
    /**
     * 构建对象
     *
     * @return 对象
     * @throws Exception ex
     */
    T makeObject() throws Exception;

    /**
     * 对象是否有效
     *
     * @param object 对象
     * @return 对象是否有效
     */
    default boolean validateObject(T object) {
        return true;
    }
}
