package com.chua.common.support.objects.definition.element;

import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.spi.ServiceProvider;
import lombok.experimental.Accessors;

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
public class ParameterDefinition implements ElementDefinition {

    private final int i;
    private final Parameter parameter;
    private final Class<?> type;

    public ParameterDefinition(int i, Parameter parameter, Class<?> type) {
        this.i = i;
        this.parameter = parameter;
        this.type = type;
    }

    @Override
    public String name() {
        return parameter.getName();
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
        return ServiceProvider.of(AnnotationResolver.class).getSpiService().get(parameter.getType());
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
}
