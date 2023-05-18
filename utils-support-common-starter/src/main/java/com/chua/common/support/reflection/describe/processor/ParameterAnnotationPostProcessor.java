package com.chua.common.support.reflection.describe.processor;


import com.chua.common.support.lang.proxy.plugin.ProxyParameterPlugin;

/**
 * 字段注解拦截器
 *
 * @author CH
 */
public interface ParameterAnnotationPostProcessor<A> extends ProxyParameterPlugin {

    /**
     * 注解类型
     *
     * @return 注解类型
     */
    Class<A> getAnnotationType();

}
