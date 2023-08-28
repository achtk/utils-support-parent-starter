package com.chua.common.support.function.strategy.name;

/**
 * oss策略
 * @author Administrator
 */
public interface OssNamedStrategy {
    /**
     * 命名
     *
     * @param name  原始名称
     * @param bytes bytes
     * @return 命名
     */
    String named(String name, byte[] bytes);
}
