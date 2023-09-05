package com.chua.common.support.objects.definition.element;

import com.chua.common.support.constant.NameConstant;
import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.objects.definition.resolver.MethodResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ObjectUtils;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 字段定义
 *
 * @author CH
 * @since 2023/09/01
 */
@Accessors(fluent = true)
public class FieldDescribe implements ElementDescribe {
    private final Field field;

    private final Class<?> type;
    private final Map<String, List<MethodDescribe>> methodDefinitions;
    private final Map<String, AnnotationDescribe> annotationDefinitions;

    public FieldDescribe(Field field, Class<?> type) {
        this.field = field;
        this.type = type;
        this.annotationDefinitions = ServiceProvider.of(AnnotationResolver.class).getSpiService().get(field);
        this.methodDefinitions = ServiceProvider.of(MethodResolver.class).getSpiService().get(type);
    }

    @Override
    public String name() {
        return field.getName();
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public Map<String, ParameterDescribe> parameters() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, AnnotationDescribe> annotations() {
        return ServiceProvider.of(AnnotationResolver.class).getSpiService().get(field.getType());
    }

    @Override
    public String returnType() {
        return field.getType().getTypeName();
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
     * get-get方法
     *
     * @return {@link MethodDescribe}
     */
    public MethodDescribe getGetMethod() {
        String name = NameConstant.METHOD_GETTER + NamingCase.toFirstUpperCase(name());
        List<MethodDescribe> methodDescribes = methodDefinitions.get(name);
        if(null == methodDescribes) {
            return null;
        }

        for (MethodDescribe methodDescribe : methodDescribes) {
            if(methodDescribe.hasParameter(getType())) {
                return methodDescribe;
            }
        }

        return null;
    }

    /**
     * 设定值
     *
     * @param value 值
     * @param bean  对象
     * @throws Exception ex
     */
    public void setValue(Object value, Object bean) throws Exception {
        if(null == value || null == bean) {
            throw new NullPointerException();
        }
        ClassUtils.setAccessible(field);
        field.set(bean, value);
    }
}
