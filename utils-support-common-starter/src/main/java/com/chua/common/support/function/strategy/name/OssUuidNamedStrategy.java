package com.chua.common.support.function.strategy.name;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.utils.IdUtils;

/**
 * oss策略
 */
@Spi("uuid")
@SpiOption("uuid")
public class OssUuidNamedStrategy implements OssNamedStrategy {
    @Override
    public String named(String name) {
        return IdUtils.uuid();
    }
}
