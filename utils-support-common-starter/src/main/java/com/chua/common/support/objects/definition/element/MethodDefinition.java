package com.chua.common.support.objects.definition.element;

import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.spi.ServiceProvider;
import lombok.experimental.Accessors;

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
public class MethodDefinition implements ElementDefinition {

    private final Method method;
    private final Class<?> type;

    public MethodDefinition(Method method, Class<?> type) {
        this.method = method;
        this.type = type;
    }

    @Override
    public String name() {
        return method.getName();
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Map<String, ParameterDefinition> parameters() {
        Map<String, ParameterDefinition> rs = new LinkedHashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            rs.put(parameter.getName(), new ParameterDefinition(i, parameter, type));
        }
        return rs;
    }

    @Override
    public Map<String, AnnotationDefinition> annotations() {
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
}
