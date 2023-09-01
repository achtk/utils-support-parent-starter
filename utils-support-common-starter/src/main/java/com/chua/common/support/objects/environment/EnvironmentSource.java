package com.chua.common.support.objects.environment;

import java.util.Map;

/**
 * 配置
 * @author CH
 */
public class EnvironmentSource {

    private final ObjectEnvironment environment;

    private final String name;

    private final Map<String, Object> definition;

    public EnvironmentSource(ObjectEnvironment environment, String name, Map<String, Object> definition) {
        this.environment = environment;
        this.name = name;
        this.definition = definition;
    }
}
