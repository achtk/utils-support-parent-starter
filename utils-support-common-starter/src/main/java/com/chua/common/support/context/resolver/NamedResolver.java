package com.chua.common.support.context.resolver;

/**
 * 命名
 *
 * @author CH
 */
public interface NamedResolver {
    /**
     * 命名
     *
     * @param type 类型
     * @return 命名
     */
    String[] named(Class<?> type);
}
