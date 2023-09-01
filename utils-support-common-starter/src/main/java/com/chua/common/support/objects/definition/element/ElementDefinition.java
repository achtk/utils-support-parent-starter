package com.chua.common.support.objects.definition.element;

import java.util.List;
import java.util.Map;

/**
 * 节点
 *
 * @author CH
 * @since 2023/09/01
 */
public interface ElementDefinition {

    /**
     * 名称
     *
     * @return {@link String}
     */
    String name();


    /**
     * get类型
     *
     * @return {@link Class}<{@link ?}>
     */
    Class<?> getType();

    /**
     * 参数
     *
     * @return {@link List}<{@link ParameterDefinition}>
     */
    Map<String, ParameterDefinition> parameters();


    /**
     * 注释
     *
     * @return {@link List}<{@link AnnotationDefinition}>
     */
    Map<String, AnnotationDefinition> annotations();

    /**
     * 返回类型
     *
     * @return {@link String}
     */
    String returnType();

    /**
     * 异常类型
     *
     * @return {@link String}
     */
    List<String> exceptionType();

    /**
     * 值
     *
     * @return 值
     */
    Map<String, Object> value();


    /**
     * 获取索引
     *
     * @return int
     */
    default int getIndex() {
        return 0;
    }
}
