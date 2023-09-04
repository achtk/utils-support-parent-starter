package com.chua.common.support.objects.environment.properties;


import java.util.List;

/**
 * 配置
 *
 * @author CH
 */
public interface MultiPropertySource extends PropertySource {

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
