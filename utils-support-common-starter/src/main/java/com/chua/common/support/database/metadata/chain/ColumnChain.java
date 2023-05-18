package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.database.entity.Column;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 解析字段
 *
 * @author CH
 */
public interface ColumnChain {
    /**
     * 处理
     *
     * @param column               column
     * @param field                字段
     * @param annotationAttributes 注解
     */
    void chain(Column column, Field field, AnnotationAttributes annotationAttributes);

    /**
     * 注解
     *
     * @return 注解
     */
    Class<? extends Annotation> annotationType();
}
