package com.chua.common.support.reflection.describe.processor;


import com.chua.common.support.reflection.describe.ParameterDescribe;

/**
 * 字段注解拦截器
 *
 * @author CH
 */
public interface ParameterAnnotationPostProcessor<A> {
    /**
     * 执行结果
     *
     * @param index             索引
     * @param parameterDescribe 字段描述
     * @param arg               参数
     * @return 结果
     */
    Object proxy(int index, ParameterDescribe parameterDescribe, Object arg);

    /**
     * 注解类型
     *
     * @return 注解类型
     */
    Class<A> getAnnotationType();

}
