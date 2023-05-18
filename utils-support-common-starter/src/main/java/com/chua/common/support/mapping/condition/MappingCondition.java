package com.chua.common.support.mapping.condition;

import com.chua.common.support.placeholder.PropertyResolver;

/**
 * 条件
 *
 * @author CH
 */
public interface MappingCondition {

    /***
     * 获取值
     * @param placeholderResolver 解释器
     * @param url 地址
     * @param name 名称
     * @return 值
     */
    String resolve(PropertyResolver placeholderResolver, String name, String url);
}
