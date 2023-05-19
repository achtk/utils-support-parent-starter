package com.chua.common.support.feature;

/**
 * 特征值
 *
 * @author CH
 */
public interface Feature<O> {
    /**
     * 特征值提取
     *
     * @param img 图片
     * @return 特征值
     */
    O predict(Object img);
}
