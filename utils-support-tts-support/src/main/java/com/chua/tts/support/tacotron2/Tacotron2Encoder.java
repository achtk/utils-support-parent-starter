package com.chua.tts.support.tacotron2;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;

/**
 * 频谱数据(声谱)
 *
 * @author CH
 */
public interface Tacotron2Encoder extends AutoCloseable {
    /**
     * 解析
     *
     * @param input 输入
     * @return 输出
     * @throws Exception ex
     */
    NDArray predict(NDList input) throws Exception;
}
