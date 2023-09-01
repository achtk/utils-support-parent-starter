package com.chua.pytorch.support;

import com.chua.common.support.engine.AbstractEngineBase;
import com.chua.common.support.feature.DetectionConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * pt引擎
 *
 * @author CH
 */
public class PytorchEngine extends AbstractEngineBase {

    private final Map<String, Object> tmp = new ConcurrentHashMap<>();

    public PytorchEngine(DetectionConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected String getPackage() {
        return "com.chua.pytorch.support";
    }

    @Override
    public <T> T newFailureInstance(Class<T> target, String type) {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
