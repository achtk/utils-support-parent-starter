package com.chua.common.support.context.aware;

import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.function.DisposableAware;

/**
 * 定义处理器
 *
 * @author CH
 */
public interface DestructionAwareBeanPostProcessor<T> extends DisposableAware {
    /**
     * 结构
     *
     * @param definition  定义
     * @param beanFactory 上下文
     */
    void postProcessBeforeDestruction(TypeDefinition definition, ConfigurableBeanFactory beanFactory);
}
