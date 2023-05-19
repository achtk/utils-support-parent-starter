package com.chua.common.support.senta;

/**
 * 情感分析
 *
 * @author CH
 */
public interface Senta extends AutoCloseable {
    /**
     * 情感分析
     *
     * @param word 單詞
     * @return 情感分析
     */
    float[] predict(String[] word);
}
