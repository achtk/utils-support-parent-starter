package com.chua.common.support.objects.definition.element;

import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ObjectUtils;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 参数定义
 *
 * @author CH
 * @since 2023/09/01
 */
@Accessors(fluent = true)
public class ParameterDescribe implements ElementDescribe {

    private final int i;
    private final Parameter parameter;
    private final Class<?> type;
    private final Map<String, AnnotationDescribe> annotations;

    public ParameterDescribe(int i, Parameter parameter, Class<?> type) {
        this.i = i;
        this.parameter = parameter;
        this.type = type;
        this.annotations = ServiceProvider.of(AnnotationResolver.class).getSpiService().get(parameter);
    }

    @Override
    public String name() {
        return parameter.getName();
    }

    @Override
    public Class<?> getType() {
        return parameter.getType();
    }

    @Override
    public Map<String, ParameterDescribe> parameters() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, AnnotationDescribe> annotations() {
        return annotations;
    }
    @Override
    public AnnotationDescribe getAnnotationDescribe(String annotationType) {
        return ObjectUtils.withNull(annotations.get(annotationType), it -> it);
    }
    @Override
    public String returnType() {
        return parameter.getType().getTypeName();
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
    public int getIndex() {
        return i;
    }

    @Override
    public void addBeanName(String name) {

    }

    /**
     * 是否包含注解
     *
     * @param annotationType param类
     * @return boolean
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return annotations.containsKey(annotationType.getTypeName());
    }

    /**
     * 是否包含注解
     *
     * @param annotationType param类
     * @return boolean
     */
    public boolean hasAnnotation(String annotationType) {
        return annotations.containsKey(annotationType);
    }

    @Override
    public Annotation getAnnotation(String annotationType) {
        return ObjectUtils.withNull(annotations.get(annotationType), AnnotationDescribe::getAnnotation);
    }

    /**
     * 可从分配
     *
     * @param targetType 目标类型
     * @return boolean
     */
    public boolean isAssignableFrom(Class<?> targetType) {
        return type.isAssignableFrom(targetType);
    }

}
