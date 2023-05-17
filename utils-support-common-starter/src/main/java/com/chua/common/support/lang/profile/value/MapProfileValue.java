package com.chua.common.support.lang.profile.value;


import com.chua.common.support.json.jsonpath.DocumentContext;
import com.chua.common.support.json.jsonpath.JsonPath;
import com.chua.common.support.lang.expression.parser.SpelExpressionParser;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.MapUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_HASH;


/**
 * profile value
 *
 * @author CH
 */
public class MapProfileValue implements ProfileValue {
    private final String resourceUrl;
    private final Map<String, Object> map;
    private DocumentContext documentContext;

    private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    public MapProfileValue(String resourceUrl) {
        this(resourceUrl, new LinkedHashMap<>());
    }

    public MapProfileValue(String resourceUrl, Map<String, Object> map) {
        this.resourceUrl = resourceUrl;
        this.map = new LinkedHashMap<>();
        map.forEach(this::add);
        this.documentContext = JsonPath.parse(map);
    }
    public MapProfileValue(String resourceUrl, Properties properties) {
       this(resourceUrl, MapUtils.asMap(properties));
    }

    @Override
    public ProfileValue add(String s, Object o) {
        map.put(s, o);
        map.put(NamingCase.toUnderlineCase(s), o);
        map.put(NamingCase.toKebabCase(s), o);
        map.put(NamingCase.toCamelCase(s), o);
        spelExpressionParser.setVariable(s, o);
        spelExpressionParser.setVariable(NamingCase.toUnderlineCase(s), o);
        spelExpressionParser.setVariable(NamingCase.toKebabCase(s), o);
        spelExpressionParser.setVariable(NamingCase.toPascalCase(s), o);
        this.documentContext = JsonPath.parse(map);
        return this;
    }


    @Override
    public String getName() {
        return resourceUrl;
    }

    @Override
    public Object getValue(String key) {
        if(null == key) {
            return null;
        }
        Object o = map.get(key);
        if(null != o) {
            return o;
        }

        try {
            String newName = key.startsWith("$.") ? key : "$." + key;
            return documentContext.read(newName);
        } catch (Exception ignored) {
        }

        try {
            String newName = key.startsWith(SYMBOL_HASH) ? key : SYMBOL_HASH + key;
            spelExpressionParser.parseExpression(newName);
        } catch (Exception ignored) {
        }

        return null;
    }

    @Override
    public boolean contains(String name) {
        if(map.containsKey(name)) {
            return true;
        }

        try {
            String newName = name.startsWith("$.") ? name : "$." + name;
            documentContext.read(newName);
            return true;
        } catch (Exception ignored) {
        }

        try {
            String newName = name.startsWith(SYMBOL_HASH) ? name : SYMBOL_HASH + name;
            spelExpressionParser.parseExpression(newName);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public void add(ProfileValue value) {
        Set<String> keys = value.keys();
        for (String key : keys) {
            add(key, value.getValue(key));
        }
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

}
