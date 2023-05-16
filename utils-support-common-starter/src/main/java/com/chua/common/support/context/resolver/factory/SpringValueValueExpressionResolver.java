package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.context.resolver.ValueExpressionResolver;
import com.chua.common.support.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.chua.common.support.context.constant.ContextConstant.AUTOWIRE;
import static com.chua.common.support.context.constant.ContextConstant.VALUE;

/**
 * 值适配器
 *
 * @author CH
 */
public class SpringValueValueExpressionResolver implements ValueExpressionResolver {

    @Override
    public String getExpression(Field field) {
        if (null == VALUE) {
            return null;
        }

        Annotation annotation = field.getDeclaredAnnotation(AUTOWIRE);
        return AnnotationUtils.asMap(annotation).getOrDefault("value", "").toString();
    }
}
