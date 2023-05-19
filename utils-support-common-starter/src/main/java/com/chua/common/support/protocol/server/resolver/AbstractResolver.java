package com.chua.common.support.protocol.server.resolver;

import com.chua.common.support.context.factory.BeanFactory;

/**
 * 解析器
 * @author CH
 */
public abstract class AbstractResolver implements Resolver{

    protected BeanFactory beanFactory;

    public AbstractResolver(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
