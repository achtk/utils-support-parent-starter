package com.chua.common.support.objects;

import com.chua.common.support.objects.definition.TypeDefinition;

/**
 * 对象管理器
 *
 * @author CH
 */
public interface ObjectContext {

    /**
     * 登记
     *
     * @param definition 释义
     */
    void register(TypeDefinition definition);
}
