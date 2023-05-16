package com.chua.common.support.context.bean;

import com.chua.common.support.collection.ConfigureAttributes;
import com.chua.common.support.context.definition.MethodDefinition;
import com.chua.common.support.context.definition.ObjectDefinition;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.execute.DefinitionExecutor;
import com.chua.common.support.context.execute.MethodDefinitionExecutor;
import com.chua.common.support.context.execute.ObjectDefinitionExecutor;
import com.chua.common.support.context.factory.BeanFactory;
import com.chua.common.support.context.parameter.ParameterAnalyzer;
import com.chua.common.support.lang.proxy.BridgingMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.reflection.describe.ParameterDescribe;
import com.chua.common.support.utils.ClassUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * 对象
 *
 * @author CH
 */
public class BeanObject {
    public static final BeanObject EMPTY = new BeanObject(null, null, null);
    private final TypeDefinition<Object> bean;
    private final Object object;
    private final BeanFactory beanFactory;
    private final Collection<ParameterAnalyzer> parameterAnalyzers;

    public BeanObject(TypeDefinition<Object> bean, Object object, BeanFactory beanFactory) {
        this.bean = bean;
        this.object = object;
        this.beanFactory = beanFactory;
        if(null == beanFactory) {
            this.parameterAnalyzers = Collections.emptyList();
        } else {
            this.parameterAnalyzers = beanFactory.getBeanMap(ParameterAnalyzer.class).values();
        }
    }


    public boolean isEmpty() {
        return null == bean || null == object;
    }

    /**
     * 执行方法
     *
     * @return 方法
     */
    public BeanObjectValue invoke(Map<Object, Object> args) {
        if (null == bean) {
            return BeanObjectValue.EMPTY;
        }

        return invoke(parameterDescribe -> {
            for (ParameterAnalyzer parameterAnalyzer : parameterAnalyzers) {
                Object value = parameterAnalyzer.analyzer(parameterDescribe, new ConfigureAttributes(args));
                if (null != value) {
                    return value;
                }
            }

            Class<?> aClass = parameterDescribe.returnClassType();
            Object onlyOneValue = ClassUtils.findOnlyOneValue(args.values(), aClass);
            if (null != onlyOneValue) {
                return onlyOneValue;
            }

            return beanFactory.getBean(aClass);
        });
    }

    /**
     * 执行方法
     *
     * @return 方法
     */
    public BeanObjectValue invoke(Object... args) {
        if (null == bean) {
            return BeanObjectValue.EMPTY;
        }

        return invoke(parameterDescribe -> {
            Class<?> aClass = parameterDescribe.returnClassType();
            if (Object[].class.isAssignableFrom(aClass)) {
                return args;
            }

            Object onlyOneValue = ClassUtils.findOnlyOneValue(args, aClass, parameterDescribe.index());
            if (null != onlyOneValue) {
                return onlyOneValue;
            }

            return beanFactory.getBean(aClass);
        });

    }


    /**
     * 执行方法
     *
     * @return 方法
     */
    public BeanObjectValue invoke(Function<ParameterDescribe, Object> function) {
        if (null == bean) {
            return BeanObjectValue.EMPTY;
        }

        DefinitionExecutor executor = null;
        if (bean instanceof MethodDefinition) {
            executor = new MethodDefinitionExecutor(beanFactory, (MethodDefinition) bean, object, function);
        } else if (bean instanceof ObjectDefinition) {
            executor = new ObjectDefinitionExecutor(beanFactory, (ObjectDefinition) bean, object, function);
        }

        if (null != executor) {
            return executor.execute();
        }

        return BeanObjectValue.EMPTY;

    }


    /**
     * 解析类型
     *
     * @param target 目标类型
     * @param <T>    类型
     * @return 对象
     */
    public <T> T resolveReference(Class<T> target) {
        return ProxyUtils.newProxy(target, new BridgingMethodIntercept<T>(target, object));
    }
}
