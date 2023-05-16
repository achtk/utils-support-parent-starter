package com.chua.common.support.context.aggregate.adaptor;

import com.chua.common.support.context.aggregate.Aggregate;
import com.chua.common.support.context.definition.TypeDefinition;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 聚合体对象适配器
 *
 * @author CH
 */
public interface AnnotationAggregateAdaptor extends AggregateAdaptor{
    /**
     * 待扫描的数据
     *
     * @return 注解
     */
    Set<Class<? extends Annotation>> getScanAnnotation();

    /**
     * 定义
     *
     * @param aggregate
     * @param annotationType 注解
     * @param type           类型
     * @return 定义
     */
    TypeDefinition<?> createDefinition(Aggregate aggregate, Class<? extends Annotation> annotationType, Class<?> type);
}
