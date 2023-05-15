package com.chua.common.support.lang.environment;


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

}
