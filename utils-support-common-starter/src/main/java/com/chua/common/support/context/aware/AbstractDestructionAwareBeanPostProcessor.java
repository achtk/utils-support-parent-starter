package com.chua.common.support.context.aware;

import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.utils.ClassUtils;

/**
 * 定义处理器
 *
 * @author CH
 */
public abstract class AbstractDestructionAwareBeanPostProcessor<T> implements DestructionAwareBeanPostProcessor<T> {

    private final String type;

    public AbstractDestructionAwareBeanPostProcessor() {
        this.type = ClassUtils.getActualTypeArguments(this.getClass())[0].getTypeName();
    }

    @Override
    public void postProcessBeforeDestruction(TypeDefinition definition, ConfigurableBeanFactory beanFactory) {
        if (!isMatch(definition)) {
            return;
        }

        afterPostProcessBeforeDestruction(definition, beanFactory);
    }

    /**
     * 是否匹配
     *
     * @param definition  定义
     * @param beanFactory 上下文
     */
    protected abstract void afterPostProcessBeforeDestruction(TypeDefinition definition, ConfigurableBeanFactory beanFactory);

    /**
     * 是否匹配
     *
     * @param definition 定义
     * @return 是否匹配
     */
    protected boolean isMatch(TypeDefinition definition) {
        String[] strings = definition.annotationTypes();
        for (String string : strings) {
            if (string.equals(type)) {
                return true;
            }
        }

        return false;
    }
}
