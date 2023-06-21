package com.chua.common.support.function;

/**
 * 对象工厂
 * @author CH
 */
public interface ObjectFactory<T> {
    /**
     * 实体化
     * @return 实体化
     */
    T newInstance();
}
