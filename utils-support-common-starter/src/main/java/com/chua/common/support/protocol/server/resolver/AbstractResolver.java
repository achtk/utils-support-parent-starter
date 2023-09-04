package com.chua.common.support.protocol.server.resolver;

import com.chua.common.support.objects.ConfigureObjectContext;

/**
 * 解析器
 * @author CH
 */
public abstract class AbstractResolver implements Resolver{

    protected ConfigureObjectContext beanFactory;

    public AbstractResolver(ConfigureObjectContext beanFactory) {
        this.beanFactory = beanFactory;
    }
}
