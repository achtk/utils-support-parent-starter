package com.chua.common.support.function.strategy.name;

/**
 * 命名策略
 *
 * @author CH
 */
public interface NamedStrategy {
    /**
     * 命名
     *
     * @param name 命名
     * @return 命名
     */
    String named(String name);
}
