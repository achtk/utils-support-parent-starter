package com.chua.common.support.mapping.condition;

import com.chua.common.support.placeholder.PropertyResolver;

/**
 * 条件
 *
 * @author CH
 */
public class SystemTimeMappingCondition implements MappingCondition {

    @Override
    public String resolve(PropertyResolver placeholderResolver, String name, String url) {
        return System.currentTimeMillis() + "";
    }
}
