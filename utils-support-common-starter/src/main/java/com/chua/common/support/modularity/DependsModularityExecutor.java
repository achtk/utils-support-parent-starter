package com.chua.common.support.modularity;

import com.chua.common.support.task.lmax.DisruptorFactory;

import java.util.Map;

/**
 * 无依赖执行器
 * @author CH
 */
public class DependsModularityExecutor<T> implements ModularityExecutor<T>{
    private final DisruptorFactory<MsgEvent> disruptorFactory;
    private final Modularity modularity;

    public DependsModularityExecutor(Modularity modularity, ModularityFactory modularityFactory) {
        this.modularity = modularity;
        this.disruptorFactory = new DisruptorFactory<>(MsgEvent::new);
    }

    @Override
    public T execute(Map<String, Object> args) {
        return null;
    }
}
