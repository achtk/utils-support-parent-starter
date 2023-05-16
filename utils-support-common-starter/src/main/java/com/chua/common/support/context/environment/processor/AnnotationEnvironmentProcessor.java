package com.chua.common.support.context.environment.processor;

import com.chua.common.support.context.environment.processor.resolver.PropertySourceResolver;
import com.chua.common.support.context.environment.property.MultiPropertySource;
import com.chua.common.support.context.environment.property.PropertiesPropertySource;
import com.chua.common.support.context.environment.property.PropertySource;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.spi.ServiceProvider;

import java.util.List;
import java.util.Map;

/**
 * 环境加载器
 *
 * @author CH
 */
public class AnnotationEnvironmentProcessor implements EnvironmentProcessor {
    @Override
    public PropertySource getPropertySource(ApplicationContextConfiguration contextConfiguration) {
        MultiPropertySource multiPropertySource = new PropertiesPropertySource();
        Map<String, PropertySourceResolver> list = ServiceProvider.of(PropertySourceResolver.class).list();
        for (PropertySourceResolver sourceResolver : list.values()) {
            List<PropertySource> propertySources = sourceResolver.getPropertySources(contextConfiguration);
            multiPropertySource.addPropertySource(propertySources);
        }
        return multiPropertySource;
    }
}
