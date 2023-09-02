package com.chua.common.support.objects.source;

import com.chua.common.support.objects.definition.TypeDefinition;

/**
 * 类型定义源
 *
 * @author CH
 * @since 2023/09/02
 */
public interface TypeDefinitionSource {


    /**
     * 匹配
     *
     * @param typeDefinition 类型定义
     * @return boolean
     */
    boolean isMatch(TypeDefinition typeDefinition);

    /**
     * 登记
     *
     * @param definition 释义
     */
    void register(TypeDefinition definition);
}
