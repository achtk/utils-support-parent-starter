package com.chua.common.support.json.jsonpath.spi.mapper;


import com.chua.common.support.converter.Converter;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.jsonpath.JsonConfiguration;
import com.chua.common.support.json.jsonpath.TypeRef;

/**
 * fastjson
 *
 * @author Administrator
 */
public class FastjsonMappingProvider implements MappingProvider {
    @Override
    public <T> T map(Object source, Class<T> targetType, JsonConfiguration configuration) {
        T necessary = Converter.convertIfNecessary(source, targetType);
        if (null != necessary) {
            return necessary;
        }
        return Json.fromJson(source.toString(), targetType);
    }

    @Override
    public <T> T map(Object source, TypeRef<T> targetType, JsonConfiguration configuration) {
        return null;
    }
}