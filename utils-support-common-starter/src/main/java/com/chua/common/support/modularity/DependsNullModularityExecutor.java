package com.chua.common.support.modularity;

import com.chua.common.support.modularity.resolver.ModularityTypeResolver;
import com.chua.common.support.spi.ServiceProvider;

import java.util.Map;

/**
 * 无依赖执行器
 * @author CH
 */
public class DependsNullModularityExecutor implements ModularityExecutor<ModularityResult>{
    private final Modularity modularity;
    private ModularityFactory modularityFactory;

    public DependsNullModularityExecutor(Modularity modularity, ModularityFactory modularityFactory) {
        this.modularity = modularity;
        this.modularityFactory = modularityFactory;
    }

    @Override
    public ModularityResult execute(Map<String, Object> args) {
        String moduleType = modularity.getModuleType();
        ModularityTypeResolver modularityTypeResolver = ServiceProvider.of(ModularityTypeResolver.class).getNewExtension(moduleType);
        return modularityTypeResolver.execute(modularityFactory, modularity, args);
    }
}
