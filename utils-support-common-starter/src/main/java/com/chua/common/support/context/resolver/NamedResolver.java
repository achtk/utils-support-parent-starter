package com.chua.common.support.context.resolver;

/**
 * 名称解析器
 * @author CH
 */
public interface NamedResolver {
    /**
     * 名称
     *
     * @param pair 注解
     * @return 名称
     */
    String[] resolve(NamePair pair);
}
