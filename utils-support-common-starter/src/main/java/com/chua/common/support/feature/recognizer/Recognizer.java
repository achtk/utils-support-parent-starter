package com.chua.common.support.feature.recognizer;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.Feature;

import java.util.List;

/**
 * 识别
 *
 * @author CH
 */
public interface Recognizer extends AutoCloseable, Feature {

    /**
     * 识别
     *
     * @param image 检测
     * @return 检测
     */
    List<PredictResult> recognize(Object image);

}
