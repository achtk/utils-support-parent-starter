package com.chua.common.support.feature.detector;

import com.chua.common.support.constant.PredictResult;

import java.io.OutputStream;
import java.util.List;

/**
 * 检测
 *
 * @author CH
 */
public interface Detector extends AutoCloseable {

    /**
     * 检测
     *
     * @param face 检测
     * @return 检测
     */
    List<PredictResult> predict(Object face);

    /**
     * 检测
     *
     * @param image        检测
     * @param outputStream 输出
     * @throws Exception ex
     */
    void detect(Object image, OutputStream outputStream) throws Exception;

}
