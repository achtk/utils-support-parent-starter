package com.chua.common.support.reflection.dynamic;

import java.util.Map;

/**
 * 注解工厂
 *
 * @author CH
 */
public interface AnnotationFactory {
    /**
     * 获取注解名称
     *
     * @param column 字段
     * @return 注解名称
     */
    String annotationName(String column);

    /**
     * 获取注解值
     *
     * @param column 字段
     * @return 注解名称
     */
    Map<String, Object> annotationValues(String column);
}
