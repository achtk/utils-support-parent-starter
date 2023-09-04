package com.chua.common.support.objects.environment.properties;

import java.util.UUID;

/**
 * 配置
 *
 * @author CH
 */
public interface SimplePropertySource extends PropertySource{
    /**
     * 名称
     *
     * @return 名称
     */
    default String getName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取值
     *
     * @param name 名称
     * @return 值
     */
    Object getProperty(String name);
}
