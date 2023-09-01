package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.objects.definition.element.AnnotationDefinition;
import com.chua.common.support.utils.ArrayUtils;

import java.lang.annotation.Annotation;
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
     * @param type 类型
     * @return {@link Map}<{@link String}, {@link AnnotationDefinition}>
     */
    Map<String, AnnotationDefinition> get(Class<?> type);


    /**
     * 默认注释解析程序
     *
     * @author CH
     * @since 2023/09/01
     */
    @Spi("default")
    class DefaultAnnotationResolver implements AnnotationResolver {

        @Override
        public Map<String, AnnotationDefinition> get(Class<?> type) {
            Annotation[] declaredAnnotations = type.getDeclaredAnnotations();
            if (ArrayUtils.isEmpty(declaredAnnotations)) {
                return Collections.emptyMap();
            }

            return Arrays.stream(declaredAnnotations).map(it -> new AnnotationDefinition(it, type)).collect(Collectors.toMap(AnnotationDefinition::name, it -> it));
        }
    }
}
