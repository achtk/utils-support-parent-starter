package com.chua.common.support.placeholder;

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

}
