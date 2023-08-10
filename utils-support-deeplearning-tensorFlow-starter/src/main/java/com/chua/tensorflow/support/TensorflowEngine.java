package com.chua.tensorflow.support;

import com.chua.common.support.engine.EngineBase;
import com.chua.common.support.feature.DetectionConfiguration;

/**
 * tensorflow
 * @author CH
 */
public class TensorflowEngine extends EngineBase {
    public TensorflowEngine(DetectionConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected String getPackage() {
        return "com.chua.tensorflow.support";
    }

    @Override
    public <T> T newFailureInstance(Class<T> target, String type) {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
