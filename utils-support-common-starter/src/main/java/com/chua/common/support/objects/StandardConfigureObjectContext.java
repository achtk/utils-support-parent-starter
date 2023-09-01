package com.chua.common.support.objects;

import com.chua.common.support.objects.environment.StandardConfigureEnvironment;

/**
 * 基础配置
 * @author CH
 * @date 2023/09/01
 */
public class StandardConfigureObjectContext implements ConfigureObjectContext{

    private final ConfigureContextConfiguration configuration;

    public StandardConfigureObjectContext(ConfigureContextConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public StandardConfigureEnvironment getEnvironment() {
        return null;
    }
}
