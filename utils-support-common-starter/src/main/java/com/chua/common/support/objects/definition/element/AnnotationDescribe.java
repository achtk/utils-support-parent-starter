package com.chua.common.support.objects.definition.element;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 注释定义
 *
 * @author CH
 * @since 2023/09/01
 */
public class AnnotationDescribe implements ElementDescribe, InitializingAware {

    private final Annotation annotation;

    private final Class<?> type;
    private Map<String, Object> value;

    public AnnotationDescribe(Annotation annotation, Class<?> type) {
        this.annotation = annotation;
        this.type = type;
        afterPropertiesSet();
    }

    @Override
    public String name() {
        Class<? extends Annotation> aClass = annotation.annotationType();
        if(Proxy.isProxyClass(annotation.annotationType())) {
            return ClassUtils.toType(aClass).getTypeName();
        }
        return aClass.getTypeName();
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Map<String, ParameterDescribe> parameters() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, AnnotationDescribe> annotations() {
        return ServiceProvider.of(AnnotationResolver.class).getSpiService().get(annotation.annotationType());
    }

    @Override
    public String returnType() {
        return annotation.annotationType().getTypeName();
    }

    @Override
    public List<String> exceptionType() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> value() {
        return value;
    }

    @Override
    public void addBeanName(String name) {

    }

    @Override
    public boolean hasAnnotation(String annotationType) {
        return type.getTypeName().equals(annotationType);
    }

    @Override
    public Annotation getAnnotation(String annotationType) {
        return type.getTypeName().equals(annotationType) ? annotation : null;
    }

    @Override
    public AnnotationDescribe getAnnotationDescribe(String annotationType) {
        return annotation.annotationType().getTypeName().equals(annotationType) ? this : null;
    }

    /**
     * 收到注解
     *
     * @return {@link Annotation}
     */
    public Annotation getAnnotation() {
        return annotation;
    }

    @Override
    public void afterPropertiesSet() {
        this.value = AnnotationUtils.getAnnotationAttributes(annotation);
    }

    /**
     * 注解是不一致
     *
     * @param annotationType 注解类型
     * @return boolean
     */
    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
        return getType().isAnnotationPresent(annotationType);
    }

    /**
     * 注解
     *
     * @return {@link T}
     */
    @SuppressWarnings("ALL")
    public <T extends Annotation> T annotation() {
        return (T) annotation;
    }
}
