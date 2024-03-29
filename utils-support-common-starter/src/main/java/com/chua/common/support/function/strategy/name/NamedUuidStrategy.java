package com.chua.common.support.function.strategy.name;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.utils.FileUtils;

import java.util.UUID;

/**
 * 命名策略
 *
 * @author CH
 * @since 2022/8/3 15:07
 */
@SpiOption("uuid")
@Spi("uuid")
public class NamedUuidStrategy implements NamedStrategy {
    @Override
    public String named(String name) {
        String extension = FileUtils.getExtension(name);
        return UUID.randomUUID().toString() + "." + extension;
    }
}
