package com.chua.common.support.mapping.filter;

import com.chua.common.support.mapping.value.MappingValue;

/**
 * 过滤器
 *
 * @author CH
 */
public interface MappingFilter<R, I> {
    /**
     * 获取降级数据
     *
     * @param name 名称
     * @param mappingValue 结果
     * @return 结果
     */
    R doFilter(String name, MappingValue mappingValue);

    /**
     * 保存数据
     * @param rs 数据
     */
    void doCache(I rs);
}
