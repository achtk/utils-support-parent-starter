package com.chua.common.support.placeholder;

import java.util.Properties;

/**
 * 混合
 *
 * @author CH
 */
public class PropertiesMixSystemPlaceholderResolver implements PlaceholderResolver, PlaceholderDynamicResolver {

    private final Properties props;
    private final SystemPropertyPlaceholderResolver placeholderResolver;

    public PropertiesMixSystemPlaceholderResolver(Properties props) {
        this.props = props;
        this.placeholderResolver = new SystemPropertyPlaceholderResolver();
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
        Object property = props.get(placeholderName);
        if (null == property) {
            return placeholderResolver.resolvePlaceholder(placeholderName);
        }

        return property.toString();
    }

    @Override
    public PlaceholderDynamicResolver add(String name, Object value) {
        if (null != value) {
            props.put(name, value);
        }
        return this;
    }
}
