package com.chua.tts.support.waveglow;

import ai.djl.ndarray.NDArray;

/**
 * 声码器
 *
 * @author CH
 */
public interface WaveGlowEncoder extends AutoCloseable {
    /**
     * 解析
     *
     * @param input 输入
     * @return 输出
     * @throws Exception ex
     */
    NDArray predict(NDArray input) throws Exception;
}
