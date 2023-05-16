package com.chua.common.support.context.definition;

import java.lang.annotation.Annotation;

/**
 * 定义
 *
 * @author CH
 */
public interface DelegateDefinition<T> extends TypeDefinition<T>{
    /**
     * 是否单例
     * @param single 是否单例
     */
    void single(boolean single);

    /**
     * 添加注解
     * @param annotation 注解
     */
    void addAnnotation(Annotation annotation);
}
