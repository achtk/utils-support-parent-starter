package com.chua.common.support.context.environment.processor;

import com.chua.common.support.context.environment.property.PropertySource;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;

/**
 * 环境加载器
 *
 * @author CH
 */
public interface EnvironmentProcessor {
    /**
     * 获取配置
     *
     * @param contextConfiguration 配置
     * @return 配置
     */
    PropertySource getPropertySource(ApplicationContextConfiguration contextConfiguration);
}
