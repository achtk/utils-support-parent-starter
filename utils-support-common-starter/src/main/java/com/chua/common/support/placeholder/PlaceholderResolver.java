package com.chua.common.support.placeholder;

/**
 * 占位符解析器
 *
 * @author CH
 */
public interface PlaceholderResolver {
    /**
     * 占位符解析
     *
     * @param placeholderName 名称
     * @return 结果
     */
    String resolvePlaceholder(String placeholderName);

}
