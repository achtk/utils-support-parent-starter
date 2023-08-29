package com.chua.common.support.extra.el.baseutil.bytecode.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 基础类
 *
 * @author CH
 */
public interface AnnotationContextFactory {
    /**
     * 获取注解
     *
     * @param annotationType 注解类
     * @param classLoader    加载器
     * @return 注解
     */
    AnnotationContext get(Class<?> annotationType, ClassLoader classLoader);

    /**
     * 获取注解
     *
     * @param resourceName resourceName
     * @param classLoader  加载器
     * @return 注解
     */
    AnnotationContext get(String resourceName, ClassLoader classLoader);

    /**
     * 获取注解
     *
     * @param method      元素
     * @param classLoader 加载器
     * @return 注解
     */
    AnnotationContext get(Method method, ClassLoader classLoader);

    /**
     * 获取注解
     *
     * @param field       元素
     * @param classLoader 加载器
     * @return 注解
     */
    AnnotationContext get(Field field, ClassLoader classLoader);

    /**
     * 获取注解
     *
     * @param annotationType 注解类
     * @return 注解
     */
    AnnotationContext get(Class<?> annotationType);

    /**
     * 获取注解
     *
     * @param resourceName resourceName
     * @return 注解
     */
    AnnotationContext get(String resourceName);

    /**
     * 获取注解
     *
     * @param method 元素
     * @return 注解
     */
    AnnotationContext get(Method method);

    /**
     * 获取注解
     *
     * @param field 元素
     * @return 注解
     */
    AnnotationContext get(Field field);

    /**
     * 获取注解
     *
     * @param annotatedElement 元素
     * @return 注解
     */
    AnnotationContext get(AnnotatedElement annotatedElement);
}
