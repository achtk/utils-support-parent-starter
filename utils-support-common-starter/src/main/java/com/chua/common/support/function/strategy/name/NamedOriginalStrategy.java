package com.chua.common.support.function.strategy.name;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;

/**
 * 原始命名策略
 *
 * @author CH
 * @since 2022/8/3 15:07
 */
@SpiOption("原始命名")
@Spi("original")
public class NamedOriginalStrategy implements NamedStrategy {
    @Override
    public String named(String name) {
        return name;
    }
}
