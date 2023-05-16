package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.context.resolver.InitializingResolver;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 初始化解析器
 *
 * @author CH
 */
public class InitializingAwareInitializingResolver implements InitializingResolver {

    private final List<Class<?>> cache = new CopyOnWriteArrayList<>();

    @Override
    public void refresh(Object bean) {
        if (!(bean instanceof InitializingAware)) {
            return;
        }

        Class<?> aClass = bean.getClass();
        if (cache.contains(aClass)) {
            return;
        }

        ((InitializingAware) bean).afterPropertiesSet();
        cache.add(aClass);
    }
}
