package com.chua.common.support.context.definition;

import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.reflection.describe.MethodDescribe;

import java.lang.annotation.Annotation;

/**
 * 定义
 *
 * @author CH
 */
public interface TypeDefinition<T> {
    /**
     * bean name
     *
     * @return bean name
     */
    String[] getBeanName();
    /**
     * bean name
     * @param name name
     * @return bean name
     */
    TypeDefinition<T> addBeanName(String... name);

    /**
     * 是否是单例
     *
     * @return 是否是单例
     */
    boolean isSingle();

    /**
     * 是否开启代理
     *
     * @return 是否开启代理
     */
    boolean isProxy();

    /**
     * 所有注解
     *
     * @return 注解
     */
    String[] annotationTypes();

    /**
     * 优先级
     *
     * @return 优先级
     */
    int order();

    /**
     * 优先级
     * @return this
     * @param order 优先级
     */
    TypeDefinition<T> order(int order);

    /**
     * 类加载器
     *
     * @return 类加载器
     */
    ClassLoader getClassLoader();

    /**
     * 是否是{type} 子类
     *
     * @param type 类型
     * @return 是否是{type} 子类
     */
    boolean isAssignableFrom(Class<?> type);

    /**
     * 是否是{type} 子类
     *
     * @param type 类型
     * @return 是否是{type} 子类
     */
    boolean isAssignableFrom(String type);

    /**
     * 获取所有类型
     *
     * @return 获取所有类型
     */
    Class<?>[] getTypes();

    /**
     * 获取所有类型
     *
     * @return 获取所有类型
     */
    Class<?>[] getSuperTypes();

    /**
     * 类型
     *
     * @return 类型
     */
    Class<?> getType();

    /**
     * 方法包含注解
     *
     * @param annotationType 注解
     * @return 方法包含注解
     */
    boolean hasMethodAnnotation(Class<? extends Annotation> annotationType);

    /**
     * 创建方法定义
     *
     * @param name 方法名称
     * @param type 类型
     * @return 方法定义
     */
    MethodDescribe createMethodDefinition(String name, Class<?>... type);

    /**
     * 获取方法类型对应的bean
     *
     * @param type 类型
     * @return rs
     */
    boolean hasMethodByParameterType(Class<?>[] type);

    /**
     * 获取对象
     *
     * @param args 参数
     * @return 对象
     */
    T getObject(Object... args);
    /**
     * 获取对象
     *
     * @param context 参数
     * @param reload 是否加载
     * @return 对象
     */
    T getObject(ConfigurableBeanFactory context, boolean reload);
    /**
     * 获取对象
     *
     * @param context 参数
     * @return 对象
     */
    default T getObject(ConfigurableBeanFactory context) {
        return getObject(context, false);
    }

    /**
     * 是否代理
     * @param proxy 是否代理
     * @return this
     */
    TypeDefinition<T> setProxy(boolean proxy);
    /**
     * 是否代理
     * @return this
     */
    default TypeDefinition<T> setProxy() {
        setProxy(true);
        return this;
    }

    /**
     * 设置对象
     * @param obj 对象
     * @return 结果
     */
    TypeDefinition<T> setObject(T obj);
}
