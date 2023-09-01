package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.objects.definition.element.AnnotationDefinition;

import java.util.List;

/**
 * 参数解析器
 *
 * @author CH
 * @since 2023/09/01
 */
public interface ParameterResolver {

    /**
     * 收到
     *
     * @param type 类型
     * @return {@link List}<{@link AnnotationDefinition}>
     */
    List<AnnotationDefinition> get(Class<?> type);


}
