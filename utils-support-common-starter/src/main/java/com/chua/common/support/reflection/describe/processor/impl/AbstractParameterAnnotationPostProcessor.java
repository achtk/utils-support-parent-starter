package com.chua.common.support.reflection.describe.processor.impl;


import com.chua.common.support.reflection.describe.ParameterDescribe;
import com.chua.common.support.reflection.describe.processor.ParameterAnnotationPostProcessor;

/**
 * 注解扫描
 *
 * @author CH
 */
public abstract class AbstractParameterAnnotationPostProcessor<A> implements ParameterAnnotationPostProcessor<A> {

    /**
     * 注解获取
     *
     * @return 注解
     */
    public A getAnnotationValue(ParameterDescribe describe) {
        return describe.getAnnotationValue(getAnnotationType());
    }
}
