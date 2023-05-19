package com.chua.common.support.feature.resolver;

import com.chua.common.support.feature.DetectionConfiguration;

/**
 * 特征值
 *
 * @author CH
 */
public abstract class AbstractResolver<T> implements Resolver<T> {

    protected DetectionConfiguration configuration;

    public AbstractResolver(DetectionConfiguration configuration) {
        System.setProperty("DJL_CACHE_DIR", configuration.cachePath());
        this.configuration = configuration;
    }
}
