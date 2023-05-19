package com.chua.common.support.feature.transfer;

import java.io.OutputStream;

/**
 * 转化器
 *
 * @author CH
 */
public interface Transfer<I> extends AutoCloseable {
    /**
     * 转化
     *
     * @param input  输入
     * @param stream 输出
     * @throws Exception ex
     */
    void transfer(I input, OutputStream stream) throws Exception;
}
