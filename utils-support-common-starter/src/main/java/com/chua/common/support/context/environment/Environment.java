package com.chua.common.support.context.environment;

import com.chua.common.support.context.environment.property.PropertySource;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.function.InitializingAware;

/**
 * 环境配置
 *
 * @author CH
 */
public interface Environment extends InitializingAware {
    /**
     * 获取参数
     *
     * @param name 名称
     * @return 值
     */
    String getProperty(String name);

    /**
     * 基础配置
     *
     * @param contextConfiguration 基础配置
     * @return 基础配置
     */
    Environment contextConfiguration(ApplicationContextConfiguration contextConfiguration);

    /**
     * 添加配置
     *
     * @param name           名称
     * @param propertySource 配置
     * @return this
     */
    Environment addPropertySource(String name, PropertySource propertySource);

    /**
     * 添加监听
     *
     * @param listener 监听
     * @return this
     */
    Environment addListener(EnvironmentListener listener);
}
