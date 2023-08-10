package com.chua.mxnet.support;

import com.chua.common.support.engine.EngineBase;
import com.chua.common.support.feature.DetectionConfiguration;

/**
 * pt引擎
 * @author CH
 */
public class MxnetEngine extends EngineBase {

    public MxnetEngine(DetectionConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    protected String getPackage() {
        return "com.chua.mxnet.support";
    }

    @Override
    public <T> T newFailureInstance(Class<T> target, String type) {
        return null;
    }
}
