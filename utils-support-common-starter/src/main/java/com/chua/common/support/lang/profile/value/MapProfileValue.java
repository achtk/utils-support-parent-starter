package com.chua.common.support.lang.profile.value;


import com.chua.common.support.function.Splitter;
import com.chua.common.support.json.jsonpath.DocumentContext;
import com.chua.common.support.json.jsonpath.JsonPath;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_XPATH;


/**
 * profile value
 *
 * @author CH
 */
public class MapProfileValue implements ProfileValue {
    private final String resourceUrl;
    private final Map map;
    private DocumentContext documentContext;

    public MapProfileValue(String resourceUrl) {
        this(resourceUrl, new LinkedHashMap());
    }

    public MapProfileValue(String resourceUrl, Map map) {
        this.resourceUrl = resourceUrl;
        this.map = map;
        this.documentContext = JsonPath.parse(map);
    }

    @Override
    public ProfileValue add(String s, Object o) {
        map.put(s, o);
        this.documentContext = JsonPath.parse(map);
        return this;
    }


    @Override
    public String getName() {
        return resourceUrl;
    }

    @Override
    public Object getValue(String key) {
        return map.get(key);
    }

    @Override
    public Object getJsonValue(String key) {
        String newKey = StringUtils.startWithMove(key, SYMBOL_XPATH);
        if (map.containsKey(newKey)) {
            return getValue(key);
        }

        try {
            return documentContext.read(key);
        } catch (Exception e) {
            return guessValue(newKey);
        }
    }

    /**
     * 猜测值
     *
     * @param key key
     * @return
     */
    private Object guessValue(String key) {
        if (map.isEmpty()) {
            return null;
        }

        Map<String, Object> guess = new LinkedHashMap<>();
        String newKey1 = key + ".*";
        map.forEach((k, v) -> {
            if (PathMatcher.INSTANCE.match(newKey1, k.toString())) {
                guess.put(k.toString(), v);
            }
        });
        if (guess.isEmpty()) {
            return guessValue2(key);
        }

        return guess;
    }

    /**
     * 猜测值
     *
     * @param key key
     * @return 值
     */
    private Object guessValue2(String key) {
        List<String> strings = Splitter.on(".").trimResults().splitToList(key);
        return getValue(strings);
    }

    /**
     * 获取值
     *
     * @param strings keys
     * @return 值
     */
    private Object getValue(List<String> strings) {
        Map tpl = map;
        int max = strings.size() - 1;
        for (int i = 0; i < strings.size(); i++) {
            String key = strings.get(i);
            Object o1 = tpl.get(key);
            if (null == o1) {
                return null;
            }

            if (max == i) {
                return o1;
            }

            if (!(o1 instanceof Map)) {
                return null;
            }

            tpl = (Map) o1;
        }

        return null;
    }
}
