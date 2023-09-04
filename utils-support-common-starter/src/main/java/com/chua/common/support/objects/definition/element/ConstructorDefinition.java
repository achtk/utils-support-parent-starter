package com.chua.common.support.objects.definition.element;

import com.chua.common.support.objects.definition.argument.ParameterArgumentResolver;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;
import com.chua.common.support.spi.ServiceProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 构造函数定义
 *
 * @author CH
 * @since 2023/09/03
 */
public class ConstructorDefinition implements ElementDefinition {
    private final Class<?> type;
    private final TypeDefinitionSourceFactory typeDefinitionSourceFactory;

    public ConstructorDefinition(Class<?> type, TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        this.type = type;
        this.typeDefinitionSourceFactory = typeDefinitionSourceFactory;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public Class<?> getType() {
        return null;
    }

    @Override
    public Map<String, ParameterDefinition> parameters() {
        return null;
    }

    @Override
    public Map<String, AnnotationDefinition> annotations() {
        return null;
    }

    @Override
    public String returnType() {
        return null;
    }

    @Override
    public List<String> exceptionType() {
        return null;
    }

    @Override
    public Map<String, Object> value() {
        return Collections.emptyMap();
    }

    @Override
    public void addBeanName(String name) {

    }


    /**
     * 新实例
     *
     * @return {@link T}
     */
    @SuppressWarnings("ALL")
    public <T> T newInstance() {
        Constructor<?>[] declaredConstructors = type.getDeclaredConstructors();
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            Object bean = newInstance(declaredConstructor, typeDefinitionSourceFactory);
            if (null != bean) {
                T bean1 = (T) bean;
                inject(bean1);
                return bean1;
            }
        }

        return null;
    }

    /**
     * 注入
     *
     * @param bean bean
     * @param <T>  类型
     */
    private <T> void inject(T bean) {
        //TODO:
    }

    private Object newInstance(Constructor<?> declaredConstructor, TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        Parameter[] parameters = declaredConstructor.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = newArgument(parameter, typeDefinitionSourceFactory);
            if (null == arg) {
                return null;
            }
            args[i] = arg;
        }

        declaredConstructor.setAccessible(true);
        try {
            return declaredConstructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object newArgument(Parameter parameter, TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        Object value = null;
        List<ParameterArgumentResolver> parameterArgumentResolvers = ServiceProvider.of(ParameterArgumentResolver.class).collect();
        for (ParameterArgumentResolver parameterArgumentResolver : parameterArgumentResolvers) {
            value = parameterArgumentResolver.resolve(parameter, typeDefinitionSourceFactory);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
