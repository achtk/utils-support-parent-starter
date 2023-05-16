package com.chua.common.support.context.resolver;

/**
 * 优先级
 *
 * @author CH
 */
public interface OrderResolver {
    /**
     * 优先级
     *
     * @param type 类型
     * @return 优先级
     */
    int order(Class<?> type);
}
