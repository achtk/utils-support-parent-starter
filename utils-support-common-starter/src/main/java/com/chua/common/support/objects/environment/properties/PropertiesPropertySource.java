package com.chua.common.support.objects.environment.properties;

import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

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
    public Object getProperty(String name) {
        return null;
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
