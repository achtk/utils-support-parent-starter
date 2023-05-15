package com.chua.common.support.collection;

import com.chua.common.support.bean.BeanBinder;
import com.chua.common.support.bean.ProfileHandler;
import com.chua.common.support.json.jsonpath.JsonPath;

import java.util.Map;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_XPATH;

/**
 * path map
 *
 * @author CH
 */
public final class PathLinkedMap extends AbstractMap implements PathMap {

    public PathLinkedMap(Map<String, Object> map) {
        super(map);
    }

    @Override
    public Object get(String key, Object defaultValue) {
        if (key.startsWith(SYMBOL_XPATH)) {
            return JsonPath.read(map, key);
        }

        KeyValue keyValue = new KeyValue(key);
        for (String k : keyValue) {
            if (map.containsKey(k)) {
                return map.getOrDefault(k, defaultValue);
            }
        }
        return defaultValue;
    }

    @Override
    public <E> E bind(String pre, Class<E> target) {
        return BeanBinder.of(new ProfileHandler() {
            @Override
            public Object getProperty(String name) {
                return map.get(name);
            }
        }).bind(pre, target).getValue();
    }
}
