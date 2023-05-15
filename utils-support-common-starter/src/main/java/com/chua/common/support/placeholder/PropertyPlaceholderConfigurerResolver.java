package com.chua.common.support.placeholder;

import java.util.Properties;

/**
 * 配置项
 *
 * @author CH
 */
public class PropertyPlaceholderConfigurerResolver implements PlaceholderResolver {

    private final Properties props;

    private PropertyPlaceholderConfigurerResolver(Properties props) {
        this.props = props;
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
        return props.getProperty(placeholderName);
    }
}
