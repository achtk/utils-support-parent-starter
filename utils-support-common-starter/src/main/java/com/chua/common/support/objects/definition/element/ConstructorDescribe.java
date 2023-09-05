package com.chua.common.support.objects.definition.element;

import com.chua.common.support.objects.definition.ClassTypeDefinition;
import com.chua.common.support.objects.definition.argument.ParameterArgumentResolver;
import com.chua.common.support.objects.inject.FieldInject;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 构造函数定义
 *
 * @author CH
 * @since 2023/09/03
 */
public class ConstructorDescribe implements ElementDescribe {
    private final ClassTypeDefinition classTypeDefinition;
    private final Class<?> type;
    private final TypeDefinitionSourceFactory typeDefinitionSourceFactory;
    private final Map<String, AnnotationDescribe> annotationDefinitions = new LinkedHashMap<>();

    public ConstructorDescribe(ClassTypeDefinition classTypeDefinition, Class<?> type, TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        this.classTypeDefinition = classTypeDefinition;
        this.type = type;
        this.typeDefinitionSourceFactory = typeDefinitionSourceFactory;
        for (AnnotationDescribe annotationDescribe : classTypeDefinition.getAnnotationDefinition()) {
            this.annotationDefinitions.put(annotationDescribe.name(), annotationDescribe);
        }

    }

    @Override
    public String name() {
        return type.getName();
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Map<String, ParameterDescribe> parameters() {
        return null;
    }

    @Override
    public Map<String, AnnotationDescribe> annotations() {
        return null;
    }

    @Override
    public String returnType() {
        return null;
    }

    @Override
    public List<String> exceptionType() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> value() {
        return Collections.emptyMap();
    }

    @Override
    public void addBeanName(String name) {

    }

    @Override
    public boolean hasAnnotation(String annotationType) {
        return annotationDefinitions.containsKey(annotationType);
    }

    @Override
    public Annotation getAnnotation(String annotationType) {
        return ObjectUtils.withNull(annotationDefinitions.get(annotationType), AnnotationDescribe::getAnnotation);
    }

    @Override
    public AnnotationDescribe getAnnotationDescribe(String annotationType) {
        return ObjectUtils.withNull(annotationDefinitions.get(annotationType), it -> it);
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
        //
        for (FieldDescribe fieldDescribe : classTypeDefinition.getFieldDefinition()) {
            MethodDescribe methodDescribe = fieldDescribe.getGetMethod();
            doInject(fieldDescribe, methodDescribe, bean);
        }
    }

    /**
     * 注入
     *
     * @param fieldDescribe  字段描述
     * @param methodDescribe 方法描述
     * @param bean           bean
     */
    private <T> void doInject(FieldDescribe fieldDescribe, MethodDescribe methodDescribe, T bean) {
        if(null != methodDescribe) {
            doInjectMethod(fieldDescribe, methodDescribe, bean);
            return;
        }

        doInjectField(fieldDescribe, bean);

    }

    /**
     * 注入字段
     *
     * @param fieldDescribe 字段描述
     * @param bean          bean
     */
    private <T> void doInjectField(FieldDescribe fieldDescribe, T bean) {
        List<FieldInject> collect = ServiceProvider.of(FieldInject.class).collect();
        for (FieldInject fieldInject : collect) {
            boolean inject;
            try {
                inject = fieldInject.inject(typeDefinitionSourceFactory, fieldDescribe, bean);
            } catch (Exception ignored) {
                continue;
            }
            if(inject) {
                break;
            }
        }
    }

    /**
     * 注入方法
     *
     * @param fieldDescribe  字段描述
     * @param methodDescribe 方法描述
     * @param bean           bean
     */
    private <T> void doInjectMethod(FieldDescribe fieldDescribe, MethodDescribe methodDescribe, T bean) {
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
