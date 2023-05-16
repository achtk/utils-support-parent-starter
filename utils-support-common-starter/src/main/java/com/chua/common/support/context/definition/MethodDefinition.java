package com.chua.common.support.context.definition;


import com.chua.common.support.reflection.describe.MethodDescribe;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 类定义
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class MethodDefinition extends ClassDefinition<Object>  {

    private final MethodDescribe methodDescribe;

    public MethodDefinition(Method method) {
        super((Class<Object>) method.getDeclaringClass());
        this.methodDescribe = MethodDescribe.of(method);
    }

    /**
     * 描述
     *
     * @return 描述
     */
    public MethodDescribe getMethodDescribe() {
        return methodDescribe;
    }

    /**
     * 执行方法
     *
     * @param object 对象
     * @param args   参数
     * @return 结果
     */
    public Object invoke(Object object, Object[] args) {
        return methodDescribe.invoke(object, args);
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        methodDescribe.addAnnotation(annotation);
    }
}
