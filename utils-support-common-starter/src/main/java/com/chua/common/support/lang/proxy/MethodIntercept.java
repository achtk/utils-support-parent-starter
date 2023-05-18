package com.chua.common.support.lang.proxy;

import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 方法拦截器
 *
 * @author CH
 */
public interface MethodIntercept<T> {
    /**
     * 是否是toString
     *
     * @param method 方法
     * @return toString
     */
    static boolean isToString(Method method) {
        return "toString".equals(method.getName());
    }

    /**
     * 是否是getClass
     *
     * @param method 方法
     * @return getClass
     */
    static boolean isGetClass(Method method) {
        return "getClass".equals(method.getName());
    }

    /**
     * 是否是 hashCode
     *
     * @param method 方法
     * @return getClass
     */
    static boolean isHashCode(Method method) {
        return "hashCode".equals(method.getName());
    }

    /**
     * 是否是 isEquals
     *
     * @param method 方法
     * @return getClass
     */
    static boolean isEquals(Method method) {
        return "equals".equals(method.getName());
    }

    /**
     * 通过 method 引用实例    Object result = method.invoke(target, args); 形式反射调用被代理类方法，
     * target 实例代表被代理类对象引用, 初始化 CglibMethodInterceptor 时候被赋值 。但是Cglib不推荐使用这种方式
     *
     * @param obj             代表Cglib 生成的动态代理类 对象本身
     * @param method          代理类中被拦截的接口方法 Method 实例
     * @param args            接口方法参数
     * @param proxy           用于调用父类真正的业务类方法。可以直接调用被代理类接口方法
     * @param proxyPluginList 插件
     */
    default void before(Object obj, Method method, Object[] args, T proxy, ProxyPlugin[] proxyPluginList) {

    }

    /**
     * 通过 method 引用实例    Object result = method.invoke(target, args); 形式反射调用被代理类方法，
     * target 实例代表被代理类对象引用, 初始化 CglibMethodInterceptor 时候被赋值 。但是Cglib不推荐使用这种方式
     *
     * @param obj             代表Cglib 生成的动态代理类 对象本身
     * @param method          代理类中被拦截的接口方法 Method 实例
     * @param args            接口方法参数
     * @param proxy           用于调用父类真正的业务类方法。可以直接调用被代理类接口方法
     * @param proxyPluginList
     * @return Object
     * @throws Throwable Throwable
     */
    Object invoke(Object obj, Method method, Object[] args, T proxy, ProxyPlugin[] proxyPluginList) throws Throwable;

    /**
     * 通过 method 引用实例    Object result = method.invoke(target, args); 形式反射调用被代理类方法，
     * target 实例代表被代理类对象引用, 初始化 CglibMethodInterceptor 时候被赋值 。但是Cglib不推荐使用这种方式
     *
     * @param obj             代表Cglib 生成的动态代理类 对象本身
     * @param method          代理类中被拦截的接口方法 Method 实例
     * @param args            接口方法参数
     * @param proxy           用于调用父类真正的业务类方法。可以直接调用被代理类接口方法
     * @param proxyPluginList
     */
    default void after(Object obj, Method method, Object[] args, T proxy, ProxyPlugin[] proxyPluginList) {

    }

    /**
     * 通过 method 引用实例    Object result = method.invoke(target, args); 形式反射调用被代理类方法，
     * target 实例代表被代理类对象引用, 初始化 CglibMethodInterceptor 时候被赋值 。但是Cglib不推荐使用这种方式
     *
     * @param obj    代表Cglib 生成的动态代理类 对象本身
     * @param method 代理类中被拦截的接口方法 Method 实例
     * @param args   接口方法参数
     * @param proxy  用于调用父类真正的业务类方法。可以直接调用被代理类接口方法
     * @return Object
     * @throws Throwable Throwable
     */
    default Object defaultInvoke(Object obj, Method method, Object[] args, T proxy) throws Throwable {
        return method.invoke(obj, args);
    }

    /**
     * 执行
     *
     * @param name 方法名
     * @param args 参数
     * @return 结果
     */
    default Object execute(String name, Object[] args) {
        return null;
    }

    /**
     * 注册
     *
     * @param name  名称
     * @param value 值
     */
    default void register(String name, Object value) {

    }
}
