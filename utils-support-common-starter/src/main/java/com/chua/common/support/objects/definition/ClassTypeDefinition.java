package com.chua.common.support.objects.definition;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.ObjectContext;
import com.chua.common.support.objects.definition.resolver.OrderResolver;
import com.chua.common.support.objects.definition.resolver.ProxyResolver;
import com.chua.common.support.objects.definition.resolver.SingleResolver;
import com.chua.common.support.spi.ServiceProvider;

/**
 * 定义
 * @author CH
 */
public class ClassTypeDefinition implements TypeDefinition, InitializingAware {


    private final Class<?> type;
    protected ObjectContext context;
    private boolean isSingle;
    private boolean isProxy;
    private int order;

    public ClassTypeDefinition(Class<?> type) {
        this.type = type;
        this.afterPropertiesSet();
    }
    public ClassTypeDefinition(Class<?> type, ObjectContext context) {
        this.type = type;
        this.context = context;
        this.afterPropertiesSet();
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Object getObject() {
        return null;
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public boolean isProxy() {
        return isProxy;
    }

    @Override
    public int order() {
        return order;
    }

    @Override
    public boolean isAssignableFrom(Class<?> target) {
        return type.isAssignableFrom(target);
    }

    @Override
    public ClassLoader getClassLoader() {
        return type.getClassLoader();
    }

    @Override
    public void afterPropertiesSet() {
        this.isSingle = ServiceProvider.of(SingleResolver.class).getSpiService().isSingle();
        this.isProxy = ServiceProvider.of(ProxyResolver.class).getSpiService().isProxy();
        this.order = ServiceProvider.of(OrderResolver.class).getSpiService().order();
    }
}
