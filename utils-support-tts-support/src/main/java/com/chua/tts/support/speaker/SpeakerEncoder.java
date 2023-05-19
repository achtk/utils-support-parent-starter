package com.chua.tts.support.speaker;

import ai.djl.ndarray.NDArray;

/**
 * 声音克隆
 *
 * @author CH
 */
public interface SpeakerEncoder extends AutoCloseable {
    /**
     * 解析
     *
     * @param ndArray array
     * @return 结果
     */
    NDArray predict(NDArray ndArray) throws Exception;
}
