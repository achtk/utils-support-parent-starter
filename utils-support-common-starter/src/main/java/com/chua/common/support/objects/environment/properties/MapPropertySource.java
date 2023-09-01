package com.chua.common.support.objects.environment.properties;

import com.chua.common.support.utils.MapUtils;

import java.util.Map;

/**
 * 配置
 *
 * @author CH
 */
public class MapPropertySource implements PropertySource {


    private final String name;
    private final Map<String, Object> map;

    public MapPropertySource(String name, Map<String, Object> map) {
        this.name = name;
        this.map = map;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getProperty(String name) {
        return MapUtils.getString(map, name);
    }
}
