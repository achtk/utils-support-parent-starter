package com.chua.common.support.objects.environment.resolver;

import com.chua.common.support.objects.environment.properties.PropertySource;

/**
 * @author CH
 */
public interface PropertySourceResolver {
    /**
     * 获取配置
     * @return 配置
     */
    PropertySource get();
}
