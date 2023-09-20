package com.chua.common.support.lang.profile.value;


import com.chua.common.support.constant.ValueMode;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.JsonPath;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.MapUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * profile value
 *
 * @author CH
 */
public class MapProfileValue implements ProfileValue {
    private final String resourceUrl;
    private final Map<String, Object> value;

    private ExpressionParser spelExpressionParser = ServiceProvider.of(ExpressionParser.class).getNewExtension("el");

    public MapProfileValue(String resourceUrl) {
        this(resourceUrl, new LinkedHashMap<>());
    }

    public MapProfileValue(String resourceUrl, Map<String, Object> map) {
        this.resourceUrl = resourceUrl;
        this.value = new LinkedHashMap<>();
        map.forEach(this::add);
    }
    public MapProfileValue(String resourceUrl, Properties properties) {
       this(resourceUrl, MapUtils.asMap(properties));
    }

    @Override
    public ProfileValue add(String s, Object o) {
        value.put(s, o);
        value.put(NamingCase.toKebabCase(s), o);
        value.put(NamingCase.toCamelCase(s), o);
        if(null != spelExpressionParser) {
            spelExpressionParser.setVariable(s, o);
            spelExpressionParser.setVariable(NamingCase.toKebabCase(s), o);
            spelExpressionParser.setVariable(NamingCase.toPascalCase(s), o);
        }
        return this;
    }


    @Override
    public String getName() {
        return resourceUrl;
    }

    @Override
    public Object getValue(String key, ValueMode valueMode) {
        if(null == key) {
            return null;
        }

        Object o = value.get(key);
        if(null != o || valueMode == ValueMode.NONE) {
            return o;
        }

        if(valueMode == ValueMode.XPATH) {
            try {
                return JsonPath.of(key).get(Json.toJson(this.value));
            } catch (Exception ignored) {
            }
            return null;
        }

        try {
            return spelExpressionParser.parseExpression(key).getValue();
        } catch (Exception ignored) {
        }

        return null;
    }

    @Override
    public boolean contains(String name, ValueMode valueMode) {
        if(value.containsKey(name)) {
            return true;
        }

        try {
             spelExpressionParser.parseExpression(name).getValue();
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
        return value.keySet();
    }

}
