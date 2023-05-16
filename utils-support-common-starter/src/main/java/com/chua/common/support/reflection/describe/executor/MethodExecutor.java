package com.chua.common.support.reflection.describe.executor;

import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.describe.processor.MethodAnnotationPostProcessor;
import com.chua.common.support.utils.ClassUtils;

import java.util.List;

/**
 * 方法执行器
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class MethodExecutor {
    private final List<MethodAnnotationPostProcessor> processors;
    private final MethodDescribe methodDescribe;

    public MethodExecutor(List<MethodAnnotationPostProcessor> processors, MethodDescribe methodDescribe) {
        this.processors = processors;
        this.methodDescribe = methodDescribe;
    }

    /**
     * 初始化
     *
     * @param processors     注解处理器
     * @param methodDescribe 方法描述
     * @return this
     */
    public static MethodExecutor of(List<MethodAnnotationPostProcessor> processors, MethodDescribe methodDescribe) {
        return new MethodExecutor(processors, methodDescribe);
    }

    /**
     * 初始化
     *
     * @param processors     注解处理器
     * @param methodDescribe 方法描述
     * @return this
     */
    public static MethodExecutor create(List<MethodAnnotationPostProcessor> processors, MethodDescribe methodDescribe) {
        return new MethodExecutor(processors, methodDescribe);
    }

    /**
     * 执行方法
     *
     * @param entity 对象
     * @param args   参数
     * @return 结果
     */
    public Object execute(Object entity, Object[] args) {
        if (processors.isEmpty()) {
            return reflect(entity, args);
        }

        return annotationRelfect(entity, args);
    }

    /**
     * 执行方法
     *
     * @param entity 对象
     * @param args   参数
     * @return 结果
     */
    private Object reflect(Object entity, Object[] args) {
        try {
            return ClassUtils.invokeMethod(methodDescribe.method(), entity, args);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 注解处理器
     *
     * @param entity 对象
     * @param args   参数
     * @return 结果
     */
    private Object annotationRelfect(Object entity, Object[] args) {
        Object rs = null;
        for (MethodAnnotationPostProcessor postProcessor : processors) {
            rs = postProcessor.execute(methodDescribe, entity, args);
        }
        return rs;
    }
}
