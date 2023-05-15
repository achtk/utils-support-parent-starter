package com.chua.common.support.json.jsonpath.internal;

import com.chua.common.support.json.jsonpath.JsonConfiguration.Defaults;
import com.chua.common.support.json.jsonpath.Option;
import com.chua.common.support.json.jsonpath.spi.json.FastjsonJsonProvider;
import com.chua.common.support.json.jsonpath.spi.json.JsonProvider;
import com.chua.common.support.json.jsonpath.spi.mapper.FastjsonMappingProvider;
import com.chua.common.support.json.jsonpath.spi.mapper.MappingProvider;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author Administrator
 */
public final class DefaultsImpl implements Defaults {

    public static final DefaultsImpl INSTANCE = new DefaultsImpl();

    private final MappingProvider mappingProvider = new FastjsonMappingProvider();

    @Override
    public JsonProvider jsonProvider() {
        return new FastjsonJsonProvider();
    }

    @Override
    public Set<Option> options() {
        return EnumSet.noneOf(Option.class);
    }

    @Override
    public MappingProvider mappingProvider() {
        return mappingProvider;
    }

    private DefaultsImpl() {
    }

}
