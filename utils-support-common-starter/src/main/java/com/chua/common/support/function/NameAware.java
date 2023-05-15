package com.chua.common.support.function;

/**
 * 命名
 *
 * @author CH
 */
@FunctionalInterface
public interface NameAware {
    /**
     * 命名
     *
     * @return 命名
     */
    String[] named();
}
