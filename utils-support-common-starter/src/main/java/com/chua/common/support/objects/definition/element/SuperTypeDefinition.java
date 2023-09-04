package com.chua.common.support.objects.definition.element;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 超类型定义
 *
 * @author CH
 * @since 2023/09/01
 */
public class SuperTypeDefinition implements ElementDefinition {
    private final Class<?> superclass;
    private final Class<?> type;

    public SuperTypeDefinition(Class<?> superclass, Class<?> type) {

        this.superclass = superclass;
        this.type = type;
    }

    @Override
    public String name() {
        return superclass.getTypeName();
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
        return null;
    }

    @Override
    public String returnType() {
        return null;
    }

    @Override
    public List<String> exceptionType() {
        return null;
    }

    @Override
    public Map<String, Object> value() {
        return null;
    }

    @Override
    public void addBeanName(String name) {

    }
}
