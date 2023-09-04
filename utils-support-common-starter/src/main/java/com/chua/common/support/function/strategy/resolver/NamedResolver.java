package com.chua.common.support.function.strategy.resolver;

/**
 * 命名解析程序
 *
 * @author CH
 * @since 2023/09/04
 */
public interface NamedResolver {

    /**
     * 解析
     *
     * @param pair 一对
     * @return {@link String[]}
     */
    String[] resolve(NamePair pair);
}
