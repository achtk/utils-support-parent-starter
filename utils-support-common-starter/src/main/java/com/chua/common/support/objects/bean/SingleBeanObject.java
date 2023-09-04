package com.chua.common.support.objects.bean;

import com.chua.common.support.objects.ObjectContext;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.definition.element.AnnotationDefinition;
import com.chua.common.support.objects.definition.element.ParameterDefinition;
import com.chua.common.support.objects.invoke.Invoke;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Function;

/**
 * 仅有一个bean对象
 *
 * @author CH
 * @since 2023/09/04
 */
public class SingleBeanObject implements BeanObject {
    private final TypeDefinition typeDefinition;
    private final ObjectContext objectContext;

    public SingleBeanObject(TypeDefinition typeDefinition, ObjectContext objectContext) {
        this.typeDefinition = typeDefinition;
        this.objectContext = objectContext;
    }

    @Override
    public Invoke newInvoke(Function<ParameterDefinition, Object> function) {
    }

    @Override
    public Invoke newInvoke(Object... args) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return null == typeDefinition;
    }

    @Override
    public <T extends Annotation> T getAnnotationValue(Class<T> annotationType) {
        List<AnnotationDefinition> annotationDefinition = typeDefinition.getAnnotationDefinition();
        for (AnnotationDefinition definition : annotationDefinition) {
            if (definition.isAnnotationPresent(annotationType)) {
                return definition.annotation();
            }
        }
        return null;
    }
}
