package com.chua.common.support.feature.transfer;

import com.chua.common.support.feature.DetectionConfiguration;

/**
 * 转化器
 *
 * @author CH
 */
public abstract class AbstractTransfer<I> implements Transfer<I> {

    protected DetectionConfiguration configuration;

    public AbstractTransfer(DetectionConfiguration configuration) {
        System.setProperty("DJL_CACHE_DIR", configuration.cachePath());
        this.configuration = configuration;
    }

}
