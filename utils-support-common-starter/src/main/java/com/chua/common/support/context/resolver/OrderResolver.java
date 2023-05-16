package com.chua.common.support.context.resolver;

/**
 * 优先级解析器
 * @author CH
 */
public interface OrderResolver {
    /**
     * 名称
     * @param namePair 注解
     * @return 名称
     */
    int resolve(NamePair namePair);
}
