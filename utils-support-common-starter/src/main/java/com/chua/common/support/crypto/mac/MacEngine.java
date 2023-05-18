package com.chua.common.support.crypto.mac;


import com.chua.common.support.constant.CommonConstant;

import java.io.IOException;
import java.io.InputStream;

import static com.chua.common.support.constant.CommonConstant.DEFAULT_BUFFER_SIZE;

/**
 * MAC（Message Authentication Code）算法引擎
 *
 * @author Looly
 * @since 4.5.13
 */
public interface MacEngine {

    /**
     * 加入需要被摘要的内容
     *
     * @param in 内容
     * @since 5.7.0
     */
    default void update(byte[] in) {
        update(in, 0, in.length);
    }

    /**
     * 加入需要被摘要的内容
     *
     * @param in    内容
     * @param inOff 内容起始位置
     * @param len   内容长度
     * @since 5.7.0
     */
    void update(byte[] in, int inOff, int len);

    /**
     * 结束并生成摘要
     *
     * @return 摘要内容
     * @since 5.7.0
     */
    byte[] doFinal();

    /**
     * 重置
     *
     * @since 5.7.0
     */
    void reset();

    /**
     * 生成摘要
     *
     * @param data         {@link InputStream} 数据流
     * @param bufferLength 缓存长度，不足1使用 {@link CommonConstant#DEFAULT_BUFFER_SIZE} 做为默认值
     * @return 摘要bytes
     */
    default byte[] digest(InputStream data, int bufferLength) {
        if (bufferLength < 1) {
            bufferLength = DEFAULT_BUFFER_SIZE;
        }

        final byte[] buffer = new byte[bufferLength];

        byte[] result;
        try {
            int read = data.read(buffer, 0, bufferLength);

            while (read > -1) {
                update(buffer, 0, read);
                read = data.read(buffer, 0, bufferLength);
            }
            result = doFinal();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            reset();
        }
        return result;
    }

    /**
     * 获取MAC算法块大小
     *
     * @return MAC算法块大小
     */
    int getMacLength();

    /**
     * 获取当前算法
     *
     * @return 算法
     */
    String getAlgorithm();
}
