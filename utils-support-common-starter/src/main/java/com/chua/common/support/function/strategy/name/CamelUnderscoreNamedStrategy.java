package com.chua.common.support.function.strategy.name;

import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.unit.name.NamingCase;

/**
 * 下划线
 * @author CH
 */
@SpiOption("下划线")
public class CamelUnderscoreNamedStrategy implements NamedStrategy{
    @Override
    public String named(String name) {
        return NamingCase.toCamelUnderscore(name);
    }
}
