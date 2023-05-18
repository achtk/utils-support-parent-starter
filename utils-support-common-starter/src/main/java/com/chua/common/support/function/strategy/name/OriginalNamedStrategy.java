package com.chua.common.support.function.strategy.name;


import com.chua.common.support.annotations.Spi;

/**
 * 命名策略
 *
 * @author CH
 */
@Spi("original")
public class OriginalNamedStrategy implements NamedStrategy {
    @Override
    public String named(String name) {
        return name;
    }
}
