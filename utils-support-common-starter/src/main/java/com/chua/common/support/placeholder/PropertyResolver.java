package com.chua.common.support.placeholder;

/**
 * 配置解析器
 *
 * @author CH
 */
public interface PropertyResolver {
    /**
     * 解析佔位符
     *
     * @param text 待解析数据
     * @return 结果
     */
    String resolvePlaceholders(String text);

    /**
     * 获取配置
     *
     * @return 配置
     */
    PlaceholderSupport getPlaceholderSupport();

    /**
     * 添加数据
     *
     * @param name  名称
     * @param value 值
     */
    void add(String name, Object value);
}
