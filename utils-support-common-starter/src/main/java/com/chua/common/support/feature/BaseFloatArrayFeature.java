package com.chua.common.support.feature;

/**
 * 特征值
 *
 * @author CH
 */
public abstract class BaseFloatArrayFeature implements Feature<float[]> {

    protected DetectionConfiguration configuration;

    public BaseFloatArrayFeature(DetectionConfiguration configuration) {
        System.setProperty("DJL_CACHE_DIR", configuration.cachePath());
        this.configuration = configuration;
    }


}
