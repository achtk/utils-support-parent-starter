package com.chua.common.support.objects.definition.element;

import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 超类型定义
 *
 * @author CH
 * @since 2023/09/01
 */
public class SuperTypeDescribe implements ElementDescribe {
    private final Class<?> superclass;
    private final Class<?> type;
    private final Map<String, AnnotationDescribe> annotationDefinitions;

    public SuperTypeDescribe(Class<?> superclass, Class<?> type) {
        this.superclass = superclass;
        this.type = type;
        this.annotationDefinitions = ServiceProvider.of(AnnotationResolver.class).getSpiService().get(superclass);
    }

    @Override
    public String name() {
        return superclass.getTypeName();
    }

    @Override
    public Class<?> getType() {
        return superclass;
    }

    @Override
    public Map<String, ParameterDescribe> parameters() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, AnnotationDescribe> annotations() {
        return annotationDefinitions;
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
}
