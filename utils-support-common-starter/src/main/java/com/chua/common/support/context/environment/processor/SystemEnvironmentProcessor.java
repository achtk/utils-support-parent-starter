package com.chua.common.support.context.environment.processor;

import com.chua.common.support.context.environment.property.*;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;

/**
 * 环境加载器
 *
 * @author CH
 */
public class SystemEnvironmentProcessor implements EnvironmentProcessor {
    @Override
    public PropertySource getPropertySource(ApplicationContextConfiguration contextConfiguration) {
        MultiPropertySource multiPropertySource = new PropertiesPropertySource();
        multiPropertySource.addPropertySource(new SystemPropertySource());
        multiPropertySource.addPropertySource(new SystemEnvPropertySource());
        return multiPropertySource;
    }
}
