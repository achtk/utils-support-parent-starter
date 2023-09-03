package com.chua.common.support.objects.definition.argument;

import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;

import java.lang.reflect.Parameter;

/**
 * 自变量解析程序
 *
 * @author CH
 * @since 2023/09/03
 */
public interface ArgumentResolver {

    /**
     * 解析
     *
     * @param parameter                   参数
     * @param typeDefinitionSourceFactory 类型定义源工厂
     * @return {@link Object}
     */
    Object resolve(Parameter parameter, TypeDefinitionSourceFactory typeDefinitionSourceFactory);
}
