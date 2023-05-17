package com.chua.common.support.view.strategy;


/**
 * 命名策略
 *
 * @author CH
 * @since 2022/8/3 15:07
 */
public interface NamedStrategy {
    /**
     * 命名
     *
     * @param name 文件名
     * @return 文件名
     */
    String named(String name);
}
