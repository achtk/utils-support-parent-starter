package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.context.resolver.InitializingResolver;
import com.chua.common.support.spi.ServiceProvider;

import java.util.Map;

/**
 * 初始化解析器
 *
 * @author CH
 */
public class InitializingResolverFactory {

    private final Map<String, InitializingResolver> list = ServiceProvider.of(InitializingResolver.class).list();

    /**
     * 刷新bean
     *
     * @param object bean
     */
    public void refresh(Object object) {
        for (InitializingResolver initializingResolver : list.values()) {
            initializingResolver.refresh(object);
        }
    }
}
