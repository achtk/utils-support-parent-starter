package com.chua.common.support.wrapper;

import java.util.Collection;

/**
 * wrapper
 * @author CH
 */
public class Wrapper<T> {
    /**
     * 初始化
     * @param collection 集合
     * @return 结果
     * @param <T> 类型
     */
    public static <T>CollectionWrapper<T> of(Collection<T> collection) {
        return new CollectionWrapper<>(collection);
    }
}
