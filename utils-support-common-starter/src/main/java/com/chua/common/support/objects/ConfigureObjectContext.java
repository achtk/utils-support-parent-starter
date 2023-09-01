package com.chua.common.support.objects;


import com.chua.common.support.objects.environment.StandardConfigureEnvironment;

/**
 * 对象管理器
 * @author CH
 */
public interface ConfigureObjectContext extends ObjectContext{
    /**
     * 获取环境
     * @return 环境
     */
    StandardConfigureEnvironment getEnvironment();
}
