package com.chua.common.support.feature.resolver;

/**
 * 特征值
 *
 * @author CH
 */
public interface Resolver<T> {
    /**
     * 特征值提取
     *
     * @param img 图片
     * @return 特征值
     */
    T resolve(T img);
}
