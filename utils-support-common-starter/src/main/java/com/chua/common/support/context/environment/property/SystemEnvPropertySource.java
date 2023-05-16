package com.chua.common.support.context.environment.property;

/**
 * 配置
 *
 * @author CH
 */
public class SystemEnvPropertySource implements PropertySource{

    @Override
    public String getName() {
        return "system-env";
    }

    @Override
    public String getProperty(String name) {
        return System.getenv(name);
    }
}
