package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.context.resolver.ValueBeanResolver;
import com.chua.common.support.context.value.BeanValue;
import com.chua.common.support.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.chua.common.support.context.constant.ContextConstant.RESOURCE_TYPE;

/**
 * 值适配器
 *
 * @author CH
 */
public class ResourceValueBeanResolver implements ValueBeanResolver {
    @Override
    public BeanValue getExpression(Field field, ConfigurableBeanFactory context) {
        if (null == RESOURCE_TYPE) {
            return null;
        }

        Annotation annotation = field.getDeclaredAnnotation(RESOURCE_TYPE);
        return new BeanValue(AnnotationUtils.asMap(annotation).getOrDefault("name", "").toString(), null != annotation);
    }
}
