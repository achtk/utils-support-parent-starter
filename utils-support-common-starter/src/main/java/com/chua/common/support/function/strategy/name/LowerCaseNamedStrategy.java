package com.chua.common.support.function.strategy.name;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;

/**
 * 命名策略
 *
 * @author CH
 */
@SpiOption("小写")
@Spi("LowerCase")
public class LowerCaseNamedStrategy implements NamedStrategy {
    @Override
    public String named(String name) {
        return name.toLowerCase();
    }
}
