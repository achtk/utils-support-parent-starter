package com.chua.common.support.function.strategy.name;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;

/**
 * 命名策略
 *
 * @author CH
 */
@SpiOption("原始名称")
@Spi("original")
public class OriginalNamedStrategy implements NamedStrategy {
    @Override
    public String named(String name) {
        return name;
    }
}
