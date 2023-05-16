package com.chua.common.support.reflection.describe.processor;

/**
 * 注解扫描
 *
 * @author CH
 */
public interface AnnotationPostProcessor<A, D> {
    /**
     * 执行结果
     *
     * @param describe 描述
     * @param entity   对象
     * @param args     参数
     * @return 结果
     */
    Object execute(D describe, Object entity, Object[] args);

}
