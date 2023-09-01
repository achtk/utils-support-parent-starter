package com.chua.common.support.objects.environment.properties;

/**
 * 配置
 *
 * @author CH
 */
public class SystemPropertySource implements PropertySource {

    @Override
    public String getName() {
        return "system";
    }

    @Override
    public String getProperty(String name) {
        return System.getProperty(name);
    }
}
