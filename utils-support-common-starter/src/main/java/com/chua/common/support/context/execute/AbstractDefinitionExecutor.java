package com.chua.common.support.context.execute;

import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.factory.BeanFactory;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.reflection.describe.ParameterDescribe;

import java.util.function.Function;

/**
 * 执行器
 *
 * @author CH
 */
public abstract class AbstractDefinitionExecutor implements DefinitionExecutor {
    protected BeanFactory beanFactory;
    protected TypeDefinition definition;
    protected Object object;
    protected Function<ParameterDescribe, Object> function;

    public AbstractDefinitionExecutor(BeanFactory beanFactory, TypeDefinition definition, Object object, Function<ParameterDescribe, Object> function) {
        this.beanFactory = beanFactory;
        this.definition = definition;
        this.object = object;
        this.function = function;
    }


    /**
     * 字段值
     *
     * @param parameterDescribe 字段描述
     * @param function          回调
     * @return 结果
     */
    protected Object[] createArgs(ParameterDescribe[] parameterDescribe, Function<ParameterDescribe, Object> function) {
        Object[] args = new Object[parameterDescribe.length];
        for (int i = 0; i < parameterDescribe.length; i++) {
            ParameterDescribe describe = parameterDescribe[i];
            Object arg = createArg(describe, function);
            args[i] = arg;
        }
        return args;
    }

    /**
     * 字段值
     *
     * @param parameterDescribe 字段描述
     * @param function          回调
     * @return 结果
     */
    protected Object createArg(ParameterDescribe parameterDescribe, Function<ParameterDescribe, Object> function) {
        Object apply = function.apply(parameterDescribe);
        if (null != apply) {
            return Converter.convertIfNecessary(apply, parameterDescribe.returnClassType());
        }

        Object factoryBean = beanFactory.getBean(parameterDescribe.returnClassType());
        if (null != factoryBean) {
            return factoryBean;
        }

        return null;
    }
}
