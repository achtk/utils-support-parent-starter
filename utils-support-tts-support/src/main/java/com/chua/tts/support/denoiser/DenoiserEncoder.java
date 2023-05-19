package com.chua.tts.support.denoiser;


import ai.djl.ndarray.NDArray;

/**
 * Denoiser
 *
 * @author Administrator
 */
public interface DenoiserEncoder extends AutoCloseable {
    /**
     * 解析
     *
     * @param input 输入
     * @return 输出
     * @throws Exception ex
     */
    NDArray predict(NDArray input) throws Exception;
}
