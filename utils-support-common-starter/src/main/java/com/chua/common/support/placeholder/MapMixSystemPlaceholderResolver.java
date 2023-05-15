package com.chua.common.support.placeholder;

import java.util.Map;

/**
 * 混合
 *
 * @author CH
 */
public class MapMixSystemPlaceholderResolver implements PlaceholderResolver, PlaceholderDynamicResolver {

    private final Map<String, Object> props;
    private final SystemPropertyPlaceholderResolver placeholderResolver;

    public MapMixSystemPlaceholderResolver(Map<String, Object> props) {
        this.props = props;
        this.placeholderResolver = new SystemPropertyPlaceholderResolver();
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
        Object objects = props.get(placeholderName);
        if (null == objects) {
            return placeholderResolver.resolvePlaceholder(placeholderName);
        }

        return objects.toString();
    }

    @Override
    public PlaceholderDynamicResolver add(String name, Object value) {
        if (null != value) {
            props.put(name, value);
        }
        return this;
    }
}
