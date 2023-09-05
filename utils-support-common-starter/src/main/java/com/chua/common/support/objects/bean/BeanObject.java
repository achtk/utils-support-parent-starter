package com.chua.common.support.objects.bean;

import com.chua.common.support.objects.definition.element.ParameterDescribe;
import com.chua.common.support.objects.invoke.Invoke;

import java.lang.annotation.Annotation;
import java.util.function.Function;

/**
 * bean对象
 *
 * @author CH
 * @since 2023/09/04
 */
public interface BeanObject {
    /**
     * 执行
     *
     * @param function o
     * @return {@link Invoke}
     */
    Invoke newInvoke(Function<ParameterDescribe, Object> function);

    /**
     * 参数
     *
     * @param args 参数
     * @return result
     */
    Invoke newInvoke(Object... args);

    /**
     * 为空
     *
     * @return boolean
     */
    boolean isEmpty();

    /**
     * 获取注解值
     *
     * @param mappingClass 映射类
     * @return {@link T}
     */
    <T extends Annotation> T getAnnotationValue(Class<T> mappingClass);
}
