package com.chua.common.support.lang.function;

/**
 * 计算相似度
 * @author CH
 */
public interface Compare {
    /**
     * 计算相似度
     * @param t1 实体1
     * @param t2 实体2
     * @return 相似度
     */
    float calculateSimilar(Object t1, Object t2);
}
