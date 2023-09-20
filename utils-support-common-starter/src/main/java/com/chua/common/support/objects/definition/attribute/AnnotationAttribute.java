package com.chua.common.support.objects.definition.attribute;

import com.chua.common.support.annotations.Alias;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.profile.ProfileReliable;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 注解属性
 *
 * @author CH
 */
public class AnnotationAttribute implements InitializingAware, ProfileReliable {
    private final Map<String, Object> value;
    private final Annotation annotation;

    public AnnotationAttribute(Map<String, Object> value, Annotation annotation) {
        this.value = value;
        this.annotation = annotation;
        afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() {
        Class<? extends Annotation> aClass = annotation.annotationType();
        ClassUtils.doWithMethods(aClass, method -> {
            if(!method.isAnnotationPresent(Alias.class)) {
                return;
            }

            String name = method.getName();
            Alias alias = method.getDeclaredAnnotation(Alias.class);
            Object o = value.get(name);
            if(!ObjectUtils.isEmpty(o)) {
                value.put(alias.value(), Converter.convertIfNecessary(o, method.getReturnType()));
            }
        });
    }

    @Override
    public Object getObject(String name) {
        return value.get(name);
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
        return null;
    }
}
