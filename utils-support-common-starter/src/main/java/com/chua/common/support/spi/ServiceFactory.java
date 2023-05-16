package com.chua.common.support.spi;

import com.chua.common.support.utils.ClassUtils;

import java.lang.reflect.Type;

/**
 * 服务工厂
 * @author CH
 */
public class ServiceFactory<E> {

    protected ServiceProvider<E> provider;

    public ServiceFactory() {
        Type[] actualTypeArguments = ClassUtils.getActualTypeArguments(this.getClass());
        provider = ServiceProvider.of((Class<E>) actualTypeArguments[0]);
    }
}
