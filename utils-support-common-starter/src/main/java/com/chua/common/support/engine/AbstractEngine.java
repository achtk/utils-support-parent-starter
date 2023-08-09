package com.chua.common.support.engine;

import com.chua.common.support.engine.config.EngineConfig;

import java.util.Arrays;

/**
 * 引擎
 *
 * @author CH
 */
public abstract class AbstractEngine<T> implements SearchEngine<T> {
    protected EngineConfig engineConfig = new EngineConfig();
    protected Class<T> target;

    public AbstractEngine(Class<T> target, EngineConfig engineConfig) {
        this.engineConfig = engineConfig;
        this.target = target;
    }

    @Override
    public SearchEngine<T> config(EngineConfig engineConfig) {
        this.engineConfig = engineConfig;
        return this;
    }


    @Override
    public boolean addAll(T... t) {
        return addAll(Arrays.asList(t));
    }


}
