package com.chua.common.support.reflection.describe.processor;


import com.chua.common.support.reflection.describe.AnnotationParameterDescribe;
import com.chua.common.support.reflection.describe.MethodDescribe;

import java.util.List;

/**
 * 注解扫描
 *
 * @author CH
 */
public interface MethodAnnotationPostProcessor<A> extends AnnotationPostProcessor<A, MethodDescribe> {
    /**
     * 注解类型
     *
     * @return 注解类型
     */
    Class<A> getAnnotationType();

    /**
     * 注解值
     *
     * @return 注解值
     */
    A getAnnotationValue();

    /**
     * 获取注解
     *
     * @return 获取注解
     */
    List<AnnotationParameterDescribe> getAnnotation();
}
