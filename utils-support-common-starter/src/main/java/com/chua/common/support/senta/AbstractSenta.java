package com.chua.common.support.senta;

import com.chua.common.support.feature.DetectionConfiguration;

/**
 * 情感分析
 *
 * @author CH
 */
public abstract class AbstractSenta implements Senta {

    protected DetectionConfiguration configuration;

    public AbstractSenta(DetectionConfiguration configuration) {
        this.configuration = configuration;
    }
}
