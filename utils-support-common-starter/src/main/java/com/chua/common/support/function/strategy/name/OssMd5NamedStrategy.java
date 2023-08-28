package com.chua.common.support.function.strategy.name;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.utils.DigestUtils;

/**
 * oss策略
 * @author Administrator
 */
@Spi("md5")
@SpiOption("md5")
public class OssMd5NamedStrategy implements OssNamedStrategy {
    @Override
    public String named(String name, byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }
}
