package com.chua.common.support.feature.recognizer;

import com.chua.common.support.feature.DetectionConfiguration;

/**
 * 检测
 *
 * @author CH
 */
public abstract class AbstractRecognizer implements Recognizer {

    protected DetectionConfiguration configuration;

    public AbstractRecognizer(DetectionConfiguration configuration) {
        System.setProperty("DJL_CACHE_DIR", configuration.cachePath());
        this.configuration = configuration;
    }

}
