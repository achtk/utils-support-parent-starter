package com.chua.common.support.function.strategy.name;

/**
 * oss策略
 */
public interface OssNamedStrategy {
    /**
     * 命名
     *
     * @param name 原始名称
     * @return 命名
     */
    String named(String name);
}
