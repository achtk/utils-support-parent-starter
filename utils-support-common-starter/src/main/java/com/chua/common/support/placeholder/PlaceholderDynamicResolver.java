package com.chua.common.support.placeholder;

import java.util.Map;

/**
 * 占位符解析器
 *
 * @author CH
 */
public interface PlaceholderDynamicResolver {
    /**
     * 添加数据
     *
     * @param name  名称
     * @param value 值
     * @return this
     */
    PlaceholderDynamicResolver add(String name, Object value);
    /**
     * 添加数据
     *
     * @param value 值
     * @return this
     */
    default PlaceholderDynamicResolver add(Map<String, ?> value) {
        value.forEach(this::add);
        return this;
    }

}
