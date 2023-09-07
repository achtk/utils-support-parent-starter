package com.chua.common.support.lang.proxy.plugin;

import com.chua.common.support.objects.definition.element.MethodDescribe;

import java.lang.reflect.Method;

/**
 * 代理插件
 *
 * @author CH
 */
public interface ProxyMethodPlugin extends ProxyPlugin{
    /**
     * 执行结果
     *
     * @param describe 描述
     * @param entity   对象
     * @param args     参数
     * @return 结果
     */
    Object execute(MethodDescribe describe, Object entity, Object[] args);

    /**
     * 有注解
     *
     * @param method 方法
     * @return boolean
     */
    boolean hasAnnotation(Method method);
}
