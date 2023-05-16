package com.chua.common.support.context.execute;

import com.chua.common.support.context.bean.BeanObjectValue;
import com.chua.common.support.context.definition.MethodDefinition;
import com.chua.common.support.context.factory.BeanFactory;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.describe.ParameterDescribe;

import java.util.function.Function;

/**
 * 执行器
 * @author CH
 */
public class MethodDefinitionExecutor extends AbstractDefinitionExecutor{


    private final MethodDescribe describe;

    public MethodDefinitionExecutor(BeanFactory beanFactory, MethodDefinition definition, Object object, Function<ParameterDescribe, Object> function) {
        super(beanFactory, definition, object, function);
        this.describe = definition.getMethodDescribe();
    }

    @Override
    public BeanObjectValue execute() {
        ParameterDescribe[] parameterDescribes = describe.parameterDescribes();
        Object[] args = new Object[parameterDescribes.length];
        for (int i = 0; i < parameterDescribes.length; i++) {
            ParameterDescribe parameterDescribe = parameterDescribes[i];
            args[i] = createArg(parameterDescribe, function);
        }

        return new BeanObjectValue(describe.invoke(object, args), describe);
    }
}
