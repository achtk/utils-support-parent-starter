package com.chua.common.support.function.strategy.name;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;

/**
 * oss策略
 */
@Spi("original")
@SpiOption("原始命名")
public class OssOriginalNamedStrategy implements OssNamedStrategy {
    @Override
    public String named(String name, byte[] bytes) {
        return name;
    }
}
