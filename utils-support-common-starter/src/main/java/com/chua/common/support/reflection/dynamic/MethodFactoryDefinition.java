package com.chua.common.support.reflection.dynamic;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.SafeFunction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * 方法工厂
 *
 * @author CH
 */
public final class MethodFactoryDefinition {

    private final Annotation[] annotations;
    private Object object;
    private final Method method;

    public static MethodFactoryDefinition of(Object object, Method method) {
        return new MethodFactoryDefinition(object, method);
    }

    public static MethodFactoryDefinition of(Method method) {
        return new MethodFactoryDefinition(method);
    }

    private MethodFactoryDefinition(Object object, Method method) {
        this(method);
        this.object = object;
    }

    private MethodFactoryDefinition(Method method) {
        method.setAccessible(true);
        this.annotations = method.getDeclaredAnnotations();
        this.method = method;
    }

    /**
     * 是否包含注解
     *
     * @param name 注解名称
     * @return 是否包含注解
     */
    public boolean hasAnnotation(String name) {
        return Arrays.stream(annotations).anyMatch(it -> it.annotationType().getName().equalsIgnoreCase(name));
    }

    /**
     * isStatic
     *
     * @return isStatic
     */
    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * 执行方法
     *
     * @param args 参数
     * @return 结果
     */
    public <T> T invoke(Object[] args) {
        return (T) invoke(args, Object.class, null);
    }


    /**
     * 执行方法
     *
     * @param args 参数
     * @param type 返回类型
     * @return 结果
     */
    public <T> T invoke(Object[] args, Class<T> type) {
        return invoke(args, type, null);
    }

    /**
     * 执行方法
     *
     * @param args     参数
     * @param type     返回类型
     * @param function 异常回调
     * @return 结果
     */
    public <T> T invoke(Object[] args, Class<T> type, SafeFunction<Throwable, T> function) {
        try {
            return Converter.convertIfNecessary(method.invoke(object, args), type);
        } catch (Exception e) {
            return null == function ? null : Converter.convertIfNecessary(function.apply(e), type);
        }
    }
}
