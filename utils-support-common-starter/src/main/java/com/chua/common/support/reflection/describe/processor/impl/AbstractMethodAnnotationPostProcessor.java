package com.chua.common.support.reflection.describe.processor.impl;

import com.chua.common.support.reflection.describe.AnnotationParameterDescribe;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.describe.processor.MethodAnnotationPostProcessor;
import com.chua.common.support.utils.ClassUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 注解扫描
 *
 * @author CH
 */
public abstract class AbstractMethodAnnotationPostProcessor<A> implements MethodAnnotationPostProcessor<A> {
    private MethodDescribe describe;

    @Override
    public Object execute(MethodDescribe describe, Object entity, Object[] args) {
        this.describe = describe;
        return execute(entity, args);
    }

    /**
     * 获取方法
     *
     * @return 方法
     */
    protected Method getMethod() {
        return describe.method();
    }

    /**
     * 执行结果
     *
     * @param entity 对象
     * @param args   参数
     * @return 结果
     */
    protected Object invoke(Object entity, Object[] args) {
        try {
            return ClassUtils.invokeMethod(describe.method(), entity, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 唯一索引
     *
     * @return 唯一索引
     */
    protected String uniqueKey() {
        return describe.uniqueKey();
    }

    /**
     * 执行结果
     *
     * @param entity 对象
     * @param args   参数
     * @return 结果
     */
    abstract Object execute(Object entity, Object[] args);

    @Override
    public A getAnnotationValue() {
        return describe.getAnnotationValue(getAnnotationType());
    }

    @Override
    public List<AnnotationParameterDescribe> getAnnotation() {
        return describe.annotation(getAnnotationType());
    }
}
