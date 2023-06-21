package com.chua.common.support.modularity.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.modularity.Modularity;
import com.chua.common.support.modularity.ModularityFactory;
import com.chua.common.support.modularity.ModularityResult;

import java.util.Map;

/**
 * 模块类型解析器
 *
 * @author CH
 */
@Spi("default")
@SuppressWarnings("ALL")
@SpiOption("默认请求处理器")
public class DefaultModularityTypeResolver implements ModularityTypeResolver {

    @Override
    public ModularityResult execute(ModularityFactory modularityFactory, Modularity modularity, Map<String, Object> args) {
        return ModularityResult.builder().data(modularity.getModuleScript()).build();
    }


}
