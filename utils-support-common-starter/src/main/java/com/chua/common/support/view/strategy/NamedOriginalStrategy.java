package com.chua.common.support.view.strategy;


import com.chua.common.support.annotations.Spi;

/**
 * 原始命名策略
 *
 * @author CH
 * @since 2022/8/3 15:07
 */
@Spi("original")
public class NamedOriginalStrategy implements NamedStrategy {
    @Override
    public String named(String name) {
        return name;
    }
}
