package com.chua.common.support.mapping.condition;

import com.chua.common.support.mapping.MappingBinder;
import com.chua.common.support.mapping.MappingConfig;

/**
 * 条件
 *
 * @author CH
 */
public class SystemTimeMappingCondition implements MappingCondition {

    @Override
    public String resolve(String name, MappingConfig mappingConfig, MappingBinder mappingBinder) {
        return System.currentTimeMillis() + "";
    }
}
