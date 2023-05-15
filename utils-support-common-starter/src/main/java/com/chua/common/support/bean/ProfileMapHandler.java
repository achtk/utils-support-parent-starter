package com.chua.common.support.bean;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.placeholder.MapMixSystemPlaceholderResolver;
import com.chua.common.support.placeholder.PlaceholderSupport;
import com.chua.common.support.placeholder.PropertyResolver;
import com.chua.common.support.placeholder.StringValuePropertyResolver;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * map
 *
 * @author CH
 */
public class ProfileMapHandler implements ProfileHandler {

    final PlaceholderSupport placeholderSupport;
    final PropertyResolver placeholderResolver;
    private final Map<? extends Object, ? extends Object> param;


    public ProfileMapHandler(Map<? extends Object, ? extends Object> param) {
        this.param = param;
        this.placeholderSupport = new PlaceholderSupport();
        this.placeholderSupport.setResolver(new MapMixSystemPlaceholderResolver((Map<String, Object>) param));
        placeholderResolver = new StringValuePropertyResolver(placeholderSupport);
    }

    @Override
    public Object getProperty(String name) {
        Object o = param.get(name);
        if (null != o) {
            return o instanceof String ? placeholderResolver.resolvePlaceholders(o.toString()) : o;
        }

        String camel = Converter.toHyphenLowerCamel(name);
        Object o1 = param.get(camel);
        return o1 instanceof String ? placeholderResolver.resolvePlaceholders(o1.toString()) : o1;
    }

    @Override
    public boolean isMatcher(String newKey) {
        for (Map.Entry<?, ?> entry : param.entrySet()) {
            if (PathMatcher.APACHE_INSTANCE.match(newKey, entry.getKey().toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> getChildren(String newKey) {
        Map<String, Object> rs = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : param.entrySet()) {
            if (PathMatcher.APACHE_INSTANCE.match(newKey, entry.getKey().toString())) {
                rs.put(entry.getKey().toString(), entry.getValue());
            }
        }
        return rs;
    }
}
