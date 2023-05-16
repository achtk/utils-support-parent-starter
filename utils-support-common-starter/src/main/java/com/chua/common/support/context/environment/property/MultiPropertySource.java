package com.chua.common.support.context.environment.property;


import com.chua.common.support.context.factory.ApplicationContextConfiguration;

import java.util.List;

/**
 * 配置
 *
 * @author CH
 */
public interface MultiPropertySource extends PropertySource {
    /**
     * 获取所有配置
     *
     * @param contextConfiguration 配置
     * @return 配置
     */
    List<PropertySource> getPropertySources(ApplicationContextConfiguration contextConfiguration);

    /**
     * 添加配置
     *
     * @param propertySource 数据源
     * @return 配置
     */
    MultiPropertySource addPropertySource(PropertySource propertySource);

    /**
     * 添加配置
     *
     * @param propertySource 数据源
     * @return 配置
     */
    MultiPropertySource addPropertySource(List<PropertySource> propertySource);

}
