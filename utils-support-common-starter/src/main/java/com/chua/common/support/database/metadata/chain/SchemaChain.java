package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 表
 *
 * @author CH
 */
public interface SchemaChain {
    /**
     * 处理
     *
     * @param reference            table
     * @param type                 类型
     * @param annotationAttributes 注解
     */
    void chain(AtomicReference<String> reference, Class<?> type, AnnotationAttributes annotationAttributes);

    /**
     * 注解
     *
     * @return 注解
     */
    Class<? extends Annotation> annotationType();
}
