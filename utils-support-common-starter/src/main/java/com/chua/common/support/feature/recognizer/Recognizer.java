package com.chua.common.support.feature.recognizer;

import com.chua.common.support.constant.PredictResult;

import java.util.List;

/**
 * 识别
 *
 * @author CH
 */
public interface Recognizer extends AutoCloseable {

    /**
     * 识别
     *
     * @param image 检测
     * @return 检测
     */
    List<PredictResult> predict(Object image);

}
