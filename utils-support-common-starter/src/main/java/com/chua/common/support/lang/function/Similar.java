package com.chua.common.support.lang.function;

/**
 * 计算相似度
 * @author CH
 */
public interface Similar<T> {
    /**
     * 计算相似度
     * @param t1 t1
     * @param t2 t2
     * @return 相似度
     */
    float calculateSimilar(T t1, T t2);
}
