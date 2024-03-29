package com.chua.common.support.objects.environment.properties;

import java.util.function.Function;

/**
 * 配置
 *
 * @author CH
 */
public class FunctionPropertySource implements PropertySource {


    private final String name;
    private final Function<String, Object> function;

    public FunctionPropertySource(String name, Function<String, Object> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getProperty(String name) {
        return function.apply(name) + "";
    }
}
