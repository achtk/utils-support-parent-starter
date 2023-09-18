package com.chua.common.support.objects.definition.element;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ObjectUtils;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import static com.chua.common.support.constant.CommonConstant.EMPTY_OBJECT;

/**
 * 方法定义
 *
 * @author CH
 * @since 2023/09/01
 */
@Accessors(fluent = true)
public class MethodDescribe implements ElementDescribe {

    @Getter
    private final Method method;
    private final Class<?> type;
    private final Map<String, AnnotationDescribe> annotationDefinitions;
    @Getter
    private Object bean;
    private String name;

    public MethodDescribe(Method method, Class<?> type, Object bean) {
        this.method = method;
        this.type = type;
        this.annotationDefinitions = ServiceProvider.of(AnnotationResolver.class).getSpiService().get(method);
        this.bean = bean;
    }
    public MethodDescribe(Method method, Class<?> type) {
        this(method, type, null);
    }

    public MethodDescribe(Method method) {
        this(method, method.getDeclaringClass(), null);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return method.getReturnType();
    }

    @Override
    public Map<String, ParameterDescribe> parameters() {
        Map<String, ParameterDescribe> rs = new LinkedHashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            rs.put(parameter.getName(), new ParameterDescribe(i, parameter, type));
        }
        return rs;
    }

    @Override
    public Map<String, AnnotationDescribe> annotations() {
        return ServiceProvider.of(AnnotationResolver.class).getSpiService().get(method.getReturnType());
    }

    @Override
    public String returnType() {
        return method.getReturnType().getTypeName();
    }

    @Override
    public List<String> exceptionType() {
        return Arrays.stream(method.getExceptionTypes()).map(Class::getTypeName).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> value() {
        return Collections.emptyMap();
    }


    @Override
    public void addBeanName(String name) {
        this.name = name;
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
     * 具有参数
     *
     * @param type 类型
     * @return boolean
     */
    public boolean hasParameter(Class<?>... type) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if(parameterTypes.length != type.length) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if(!parameterType.isAssignableFrom(type[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     *执行
     *
     * @param value 价值
     * @param bean  bean
     * @return {@link Object}
     * @throws Exception 异常
     */
    public Object execute(Object bean, Object[] value) throws Exception {
        if(null == value || null == bean) {
            throw new NullPointerException();
        }

        ClassUtils.setAccessible(method);
        return method.invoke(bean, value);
    }

    /**
     * 自我执行
     *
     * @param targetType 目标类型
     * @return {@link T}
     */
    public <T>T executeSelf(Class<T> targetType) {
        try {
            return Converter.convertIfNecessary(executeSelf(), targetType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 自我执行
     *
     * @return {@link Object}
     */
    public Object executeSelf() {
        try {
            return execute(bean, EMPTY_OBJECT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 有bean
     *
     * @return boolean
     */
    public boolean hasBean() {
        return bean != null;
    }


    /**
     * 登记
     *
     * @param bean bean
     */
    public void register(Object bean) {
        this.bean = bean;
    }
}
