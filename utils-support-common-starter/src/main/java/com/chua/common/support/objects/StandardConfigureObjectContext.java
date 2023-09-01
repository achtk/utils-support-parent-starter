package com.chua.common.support.objects;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.environment.StandardConfigureEnvironment;

/**
 * 基础配置
 * @author CH
 * @date 2023/09/01
 */
public class StandardConfigureObjectContext implements ConfigureObjectContext, InitializingAware {

    private final ConfigureContextConfiguration configuration;
    private StandardConfigureEnvironment configureEnvironment;

    public StandardConfigureObjectContext(ConfigureContextConfiguration configuration) {
        this.configuration = configuration;
        this.afterPropertiesSet();
    }

    @Override
    public StandardConfigureEnvironment getEnvironment() {
        return configureEnvironment;
    }

    @Override
    public void afterPropertiesSet() {
        this.configureEnvironment = new StandardConfigureEnvironment(configuration.environmentConfiguration());
    }
}
