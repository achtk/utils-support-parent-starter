package com.chua.common.support.objects.definition.element;

import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ObjectUtils;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 方法定义
 *
 * @author CH
 * @since 2023/09/01
 */
@Accessors(fluent = true)
public class MethodDescribe implements ElementDescribe {

    private final Method method;
    private final Class<?> type;
    private final Map<String, AnnotationDescribe> annotationDefinitions;
    private String name;

    public MethodDescribe(Method method, Class<?> type) {
        this.method = method;
        this.type = type;
        this.annotationDefinitions = ServiceProvider.of(AnnotationResolver.class).getSpiService().get(method);
    }

    public MethodDescribe(Method method) {
        this(method, method.getDeclaringClass());
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
     * 处决
     *
     * @param value 价值
     * @param bean  bean
     */
    public void execute(Object bean, Object[] value) throws Exception {
        if(null == value || null == bean) {
            throw new NullPointerException();
        }

        ClassUtils.setAccessible(method);
        method.invoke(bean, value);
    }
}
