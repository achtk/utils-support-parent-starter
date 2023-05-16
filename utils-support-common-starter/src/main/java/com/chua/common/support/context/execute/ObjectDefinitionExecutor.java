package com.chua.common.support.context.execute;

import com.chua.common.support.context.bean.BeanObjectValue;
import com.chua.common.support.context.definition.ObjectDefinition;
import com.chua.common.support.context.factory.BeanFactory;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.describe.ParameterDescribe;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.value.Value;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 执行器
 * @author CH
 */
public class ObjectDefinitionExecutor extends AbstractDefinitionExecutor{

    private Class<? extends Object> aClass;

    public ObjectDefinitionExecutor(BeanFactory beanFactory, ObjectDefinition definition, Object object, Function<ParameterDescribe, Object> function) {
        super(beanFactory, definition, object, function);
        if(null != object) {
           this.aClass = object.getClass();
        }
    }

    @Override
    public BeanObjectValue execute() {
        if(null == object) {
            return BeanObjectValue.EMPTY;
        }

        Method[] declaredMethods = aClass.getDeclaredMethods();
        Value<Object> objectDefaultValue = findMethod(declaredMethods);
        if(null == objectDefaultValue) {
            return BeanObjectValue.EMPTY;
        }

        MethodDescribe methodDescribe = (MethodDescribe) objectDefaultValue.getValue();
        return new BeanObjectValue(methodDescribe.invoke(object, (Object[]) objectDefaultValue.getValue()), methodDescribe);
    }

    /**
     * 获取参数
     * @param declaredMethods declaredMethods
     * @return declaredMethods
     */
    private Value<Object> findMethod(Method[] declaredMethods) {
        if(declaredMethods.length == 0) {
            return null;
        }

        if(declaredMethods.length == 1) {
            MethodDescribe methodDescribe = MethodDescribe.of( declaredMethods[0]);
            ParameterDescribe[] parameterDescribes = methodDescribe.parameterDescribes();
            Object[] value = createArgs(parameterDescribes, function);
            return Value.of(methodDescribe, value);
        }

        for (Method declaredMethod : declaredMethods) {
            MethodDescribe methodDescribe = MethodDescribe.of(declaredMethod);
            ParameterDescribe[] parameterDescribes = methodDescribe.parameterDescribes();
            Object[] value = createArgs(parameterDescribes, function);
            if(ArrayUtils.allEmpty(value)) {
                continue;
            }
            return Value.of(methodDescribe, value);
        }
        return null;
    }
}
