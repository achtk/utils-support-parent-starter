package com.chua.common.support.objects.definition.element;

import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.spi.ServiceProvider;
import lombok.experimental.Accessors;

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
public class FieldDefinition implements ElementDefinition {
    private final Field field;

    private Class<?> type;

    public FieldDefinition(Field field, Class<?> type) {
        this.field = field;
        this.type = type;
    }

    @Override
    public String name() {
        return field.getName();
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
}
