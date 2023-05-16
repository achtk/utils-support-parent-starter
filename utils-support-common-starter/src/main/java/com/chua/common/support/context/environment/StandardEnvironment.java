package com.chua.common.support.context.environment;

import com.chua.common.support.context.environment.processor.EnvironmentProcessor;
import com.chua.common.support.context.environment.property.MultiPropertySource;
import com.chua.common.support.context.environment.property.PropertySource;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.spi.ServiceProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 环境配置
 *
 * @author CH
 */
public class StandardEnvironment implements Environment {

    final List<EnvironmentListener> list = new LinkedList<>();

    final EnvironmentSource environmentSource = new EnvironmentSource(list, this);
    private ApplicationContextConfiguration contextConfiguration;

    @Override
    public String getProperty(String name) {
        String value = null;
        for (PropertySource propertySource : environmentSource.values()) {
            String property = propertySource.getProperty(name);
            if (null == property) {
                continue;
            }
            value = property;
        }
        return value;
    }

    @Override
    public Environment contextConfiguration(ApplicationContextConfiguration contextConfiguration) {
        this.contextConfiguration = contextConfiguration;
        return this;
    }

    @Override
    public Environment addPropertySource(String name, PropertySource propertySource) {
        environmentSource.put(name, propertySource);
        return this;
    }

    @Override
    public Environment addListener(EnvironmentListener listener) {
        this.list.add(listener);
        return this;
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, EnvironmentProcessor> processorMap = ServiceProvider.of(EnvironmentProcessor.class).list();
        for (EnvironmentProcessor environmentProcessor : processorMap.values()) {
            PropertySource propertySource = environmentProcessor.getPropertySource(contextConfiguration);
            if (null == propertySource) {
                continue;
            }

            if (propertySource instanceof MultiPropertySource) {
                for (PropertySource source : ((MultiPropertySource) propertySource).getPropertySources(contextConfiguration)) {
                    environmentSource.put(source.getName(), source);
                }
                continue;
            }

            environmentSource.put(propertySource.getName(), propertySource);
        }
    }
}
