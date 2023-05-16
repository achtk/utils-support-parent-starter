package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.context.resolver.InitializingResolver;
import com.chua.common.support.reflection.MethodStation;

import static com.chua.common.support.context.constant.ContextConstant.INITIALIZING_BEAN;

/**
 * 初始化解析器
 *
 * @author CH
 */
public class SpringInitializingBeanInitializingResolver implements InitializingResolver {
    @Override
    public void refresh(Object bean) {
        if (!INITIALIZING_BEAN.isAssignableFrom(bean.getClass())) {
            return;
        }

        MethodStation.of(bean).invoke("afterPropertiesSet");
    }
}
