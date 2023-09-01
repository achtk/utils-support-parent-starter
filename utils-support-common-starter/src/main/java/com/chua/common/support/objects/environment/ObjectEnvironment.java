package com.chua.common.support.objects.environment;

/**
 * 环境
 * @author CH
 */
public interface ObjectEnvironment {

    /**
     * 得获取值
     *
     * @param name 名字
     * @return {@link Object}
     */
    Object get(String name);


    /**
     * 获取环境变量
     * @param configuration 配置
     * @return this
     */
    ObjectEnvironment getEnvironment(EnvironmentConfiguration configuration);
}
