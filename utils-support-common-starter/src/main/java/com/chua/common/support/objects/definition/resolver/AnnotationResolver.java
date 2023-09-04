package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.objects.definition.element.AnnotationDefinition;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 注释解析器
 *
 * @author CH
 * @since 2023/09/01
 */
@Spi
public interface AnnotationResolver {

    /**
     * 收到
     *
     * @param member 类型
     * @return {@link Map}<{@link String}, {@link AnnotationDefinition}>
     */
    Map<String, AnnotationDefinition> get(Object member);


    /**
     * 默认注释解析程序
     *
     * @author CH
     * @since 2023/09/01
     */
    @Spi("default")
    class DefaultAnnotationResolver implements AnnotationResolver {

        @Override
        public Map<String, AnnotationDefinition> get(Object member) {
            Annotation[] declaredAnnotations = null;
            if (member instanceof Class<?>) {
                declaredAnnotations = ((Class<?>) member).getDeclaredAnnotations();
            } else if (member instanceof Method) {
                declaredAnnotations = ((Method) member).getDeclaredAnnotations();
            } else if (member instanceof Field) {
                declaredAnnotations = ((Field) member).getDeclaredAnnotations();
            } else if (member instanceof Parameter) {
                declaredAnnotations = ((Parameter) member).getDeclaredAnnotations();
            } else if (member instanceof Constructor<?>) {
                declaredAnnotations = ((Constructor<?>) member).getDeclaredAnnotations();
            } else if (member instanceof Annotation) {
                declaredAnnotations = ((Annotation) member).annotationType().getAnnotations();
            }

            if (ArrayUtils.isEmpty(declaredAnnotations)) {
                return Collections.emptyMap();
            }

            return Arrays.stream(declaredAnnotations).map(it -> new AnnotationDefinition(it, ClassUtils.toType(member))).collect(Collectors.toMap(AnnotationDefinition::name, it -> it));
        }
    }
}
