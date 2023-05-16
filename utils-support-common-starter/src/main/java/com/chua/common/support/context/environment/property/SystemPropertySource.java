package com.chua.common.support.context.environment.property;

/**
 * 配置
 *
 * @author CH
 */
public class SystemPropertySource implements PropertySource{

    @Override
    public String getName() {
        return "system";
    }

    @Override
    public String getProperty(String name) {
        return System.getProperty(name);
    }
}
