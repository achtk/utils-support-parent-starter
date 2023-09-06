package com.chua.common.support.mapping;

import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyMethod;
import com.chua.common.support.lang.proxy.ProxyUtils;

import java.util.function.Function;

/**
 * 实体映射
 * @author CH
 */
public class HttpMapping<T> extends AbstractMapping<T>{

    public HttpMapping(Class<T> beanType) {
        super(beanType);
    }


    @Override
    public T get() {
        return ProxyUtils.proxy(beanType, beanType.getClassLoader(), new DelegateMethodIntercept<>(beanType, new Function<ProxyMethod, Object>() {
            @Override
            public Object apply(ProxyMethod proxyMethod) {
                return null;
            }
        }));
    }
}
