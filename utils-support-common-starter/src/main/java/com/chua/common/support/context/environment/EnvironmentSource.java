package com.chua.common.support.context.environment;

import com.chua.common.support.constant.Action;
import com.chua.common.support.context.environment.property.PropertySource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 环境变量
 * @author CH
 */
public class EnvironmentSource extends ConcurrentHashMap<String, PropertySource> {

    private final List<EnvironmentListener> list;
    private final Environment environment;

    public EnvironmentSource(List<EnvironmentListener> list, Environment environment) {
        this.list = list;
        this.environment = environment;
    }

    @Override
    public synchronized PropertySource put(String key, PropertySource value) {
        for (EnvironmentListener environmentListener : list) {
            String property = value.getProperty(environmentListener.getExpression());
            if(null == property) {
                continue;
            }

            environmentListener.doListener(environment.getProperty(environmentListener.getExpression()), Action.UPDATE);
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends PropertySource> m) {
        m.forEach(this::put);
    }
}
