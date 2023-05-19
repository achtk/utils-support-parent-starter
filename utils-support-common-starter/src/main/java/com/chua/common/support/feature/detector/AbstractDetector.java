package com.chua.common.support.feature.detector;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.lang.profile.DelegateProfile;

/**
 * 检测
 *
 * @author CH
 */
public abstract class AbstractDetector extends DelegateProfile implements Detector {

    protected DetectionConfiguration configuration;

    public AbstractDetector(DetectionConfiguration configuration) {
        if(null == configuration) {
            return;
        }
        System.setProperty("DJL_CACHE_DIR", configuration.cachePath());
        System.setProperty("ENGINE_CACHE_DIR", configuration.cachePath());
        this.configuration = configuration;
    }

}
