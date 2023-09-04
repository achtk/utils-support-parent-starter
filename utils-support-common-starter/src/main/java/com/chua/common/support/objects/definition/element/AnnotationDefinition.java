package com.chua.common.support.objects.definition.element;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 注释定义
 *
 * @author CH
 * @since 2023/09/01
 */
public class AnnotationDefinition implements ElementDefinition, InitializingAware {

    private final Annotation annotation;

    private final Class<?> type;
    private Map<String, Object> value;

    public AnnotationDefinition(Annotation annotation, Class<?> type) {
        this.annotation = annotation;
        this.type = type;
        afterPropertiesSet();
    }

    @Override
    public String name() {
        return annotation.toString();
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Map<String, ParameterDefinition> parameters() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, AnnotationDefinition> annotations() {
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
