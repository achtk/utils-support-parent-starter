package com.chua.common.support.function.strategy.name;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.date.DateTime;

/**
 * 命名策略
 *
 * @author CH
 * @since 2022/8/3 15:07
 */
@Spi("date")
public class NamedTimeStrategy implements NamedStrategy {
    @Override
    public String named(String name) {
        DateTime dateTime = DateTime.now();
        String path = dateTime.toString("yyyyMMdd");
        return path + "/" + name;
    }
}
