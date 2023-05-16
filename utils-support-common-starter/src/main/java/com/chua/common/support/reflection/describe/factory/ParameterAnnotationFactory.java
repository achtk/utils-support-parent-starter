package com.chua.common.support.reflection.describe.factory;

import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.describe.ParameterDescribe;
import com.chua.common.support.reflection.describe.processor.ParameterAnnotationPostProcessor;
import com.chua.common.support.utils.ArrayUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 参数注解工厂
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class ParameterAnnotationFactory {
    private List<ParameterAnnotationPostProcessor> parameters;

    public ParameterAnnotationFactory(List<ParameterAnnotationPostProcessor> parameters) {
        this.parameters = parameters;
    }

    public static ParameterAnnotationFactory of(List<ParameterAnnotationPostProcessor> parameters) {
        return new ParameterAnnotationFactory(parameters);
    }

    /**
     * 处理注解
     *
     * @param methodDescribe 方法注解
     * @param args           参数
     * @return 注解
     */
    public Object[] execute(MethodDescribe methodDescribe, Object[] args) {

        List<Object> rs = new LinkedList<>();
        ParameterDescribe[] parameterDescribes = methodDescribe.parameterDescribes();
        for (int i = 0; i < parameterDescribes.length; i++) {
            ParameterDescribe parameterDescribe = parameterDescribes[i];
            rs.add(make(i, parameterDescribe, ArrayUtils.getIndex(args, i)));
        }


        return rs.toArray();
    }

    /**
     * 处理参数
     *
     * @param index             索引
     * @param parameterDescribe 参数描述
     * @param arg               参数
     * @return 结果
     */
    private Object make(int index, ParameterDescribe parameterDescribe, Object arg) {
        if (parameters.isEmpty()) {
            return arg;
        }


        for (ParameterAnnotationPostProcessor parameterAnnotationPostProcessor : parameters) {
            Class annotationType = parameterAnnotationPostProcessor.getAnnotationType();
            if (parameterDescribe.hasAnnotation(annotationType)) {
                arg = parameterAnnotationPostProcessor.proxy(index, parameterDescribe, arg);
            }
        }

        return arg;
    }
}
