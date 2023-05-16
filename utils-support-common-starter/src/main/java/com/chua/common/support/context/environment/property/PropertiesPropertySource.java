package com.chua.common.support.context.environment.property;

import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * 配置
 *
 * @author CH
 */
@NoArgsConstructor
public class PropertiesPropertySource implements MultiPropertySource {

    private List<PropertySource> propertySources;

    public PropertiesPropertySource(List<PropertySource> propertySources) {
        this.propertySources = propertySources;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getProperty(String name) {
        return null;
    }

    @Override
    public List<PropertySource> getPropertySources(ApplicationContextConfiguration contextConfiguration) {
        return Optional.ofNullable(propertySources).orElse(Collections.emptyList());
    }

    @Override
    public MultiPropertySource addPropertySource(PropertySource propertySource) {
        if (null == propertySources) {
            synchronized (this) {
                if (null == propertySources) {
                    propertySources = new LinkedList<>();
                }
            }
        }
        propertySources.add(propertySource);
        return this;
    }

    @Override
    public MultiPropertySource addPropertySource(List<PropertySource> propertySource) {
        if (null == propertySource) {
            return this;
        }
        for (PropertySource source : propertySource) {
            addPropertySource(source);
        }
        return this;
    }


}
