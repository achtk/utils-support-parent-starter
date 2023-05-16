package com.chua.common.support.context.environment.property;

/**
 * 配置
 *
 * @author CH
 */
public interface PropertySource {
    /**
     * 名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 获取值
     *
     * @param name 名称
     * @return 值
     */
    String getProperty(String name);
}
