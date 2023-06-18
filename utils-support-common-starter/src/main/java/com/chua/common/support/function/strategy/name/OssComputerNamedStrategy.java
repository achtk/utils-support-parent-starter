package com.chua.common.support.function.strategy.name;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.utils.IdUtils;

/**
 * oss策略
 */
@Spi("createComputer")
@SpiOption("uuid + 主板信息")
public class OssComputerNamedStrategy implements OssNamedStrategy {
    @Override
    public String named(String name, byte[] bytes) {
        return IdUtils.createTid();
    }
}
