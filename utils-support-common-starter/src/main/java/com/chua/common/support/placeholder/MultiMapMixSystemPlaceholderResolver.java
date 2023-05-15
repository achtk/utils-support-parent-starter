package com.chua.common.support.placeholder;

import com.chua.common.support.collection.MultiValueMap;
import com.chua.common.support.json.Json;

import java.util.Collection;

/**
 * 混合
 *
 * @author CH
 */
public class MultiMapMixSystemPlaceholderResolver implements PlaceholderResolver, PlaceholderDynamicResolver {

    private final MultiValueMap<String, Object> props;
    private final SystemPropertyPlaceholderResolver placeholderResolver;

    public MultiMapMixSystemPlaceholderResolver(MultiValueMap<String, Object> props) {
        this.props = props;
        this.placeholderResolver = new SystemPropertyPlaceholderResolver();
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
        Collection<Object> objects = props.get(placeholderName);
        if (objects.isEmpty()) {
            return placeholderResolver.resolvePlaceholder(placeholderName);
        }

        return Json.toJson(objects);
    }

    @Override
    public PlaceholderDynamicResolver add(String name, Object value) {
        if (null != value) {
            props.add(name, value);
        }
        return this;
    }
}
