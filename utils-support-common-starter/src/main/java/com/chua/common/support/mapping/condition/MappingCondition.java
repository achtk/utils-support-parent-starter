package com.chua.common.support.mapping.condition;

import com.chua.common.support.mapping.MappingBinder;
import com.chua.common.support.mapping.MappingConfig;

/**
 * 条件
 *
 * @author CH
 */
public interface MappingCondition {

    /**
     * 获取值
     *
     * @param name          名称
     * @param mappingConfig 映射配置
     * @param mappingBinder 制图活页夹
     * @return 值
     */
    String resolve(String name, MappingConfig mappingConfig, MappingBinder mappingBinder);
}
