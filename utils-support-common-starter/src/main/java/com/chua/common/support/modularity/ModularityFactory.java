package com.chua.common.support.modularity;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COLON;

/**
 * 模块
 *
 * @author CH
 */
public class ModularityFactory implements InitializingAware {

    static final Map<String, Modularity> MODULARITY_DATA = new ConcurrentHashMap<>();
    AtomicBoolean status = new AtomicBoolean(false);

    private ModularityFactory() {
    }

    /**
     * 初始化
     *
     * @return 初始化
     */
    public static ModularityFactory create() {
        ModularityFactory modularityFactory = new ModularityFactory();
        modularityFactory.afterPropertiesSet();
        return modularityFactory;
    }

    /**
     * 注册模块
     *
     * @param modularity 模块
     * @return 结果
     */
    public ModularityFactory register(Modularity modularity) {
        Preconditions.checkNotNull(modularity.getModuleType());
        Preconditions.checkNotNull(modularity.getModuleName());
        MODULARITY_DATA.put(modularity.getModuleType() + ":" + modularity.getModuleName(), modularity);
        return this;
    }

    /**
     * 注销模块
     *
     * @param modularity 模块
     * @return 结果
     */
    public ModularityFactory unregister(Modularity modularity) {
        MODULARITY_DATA.remove(modularity.getModuleType() + ":" + modularity.getModuleName());
        return this;
    }

    /**
     * 注销模块
     *
     * @param moduleName 模块名称
     * @param moduleType 模块类型
     * @return 结果
     */
    public ModularityFactory unregister(String moduleType, String moduleName) {
        MODULARITY_DATA.remove(moduleType + ":" + moduleName);
        return this;
    }

    @Override
    public void afterPropertiesSet() {
        if (status.get()) {
            return;
        }
        status.set(true);
        ServiceProvider.of(ModularityResolver.class).forDefinitionEach(it -> {
            Modularity modularity = Modularity.builder().build();
            String name = it.getName();
            List<String> strings = Splitter.on(SYMBOL_COLON).limit(2).splitToList(name);
            modularity.setModuleName(strings.get(1));
            modularity.setModuleType(strings.get(0));
            ModularityDag modularityDag = it.getImplClass().getDeclaredAnnotation(ModularityDag.class);
            if (null != modularityDag) {
                modularity.setModuleDepends(modularityDag.value());
            }

            ModularityScript modularityScript = it.getImplClass().getDeclaredAnnotation(ModularityScript.class);
            if (null != modularityScript) {
                modularity.setModuleScript(modularityScript.value());
            }

            register(modularity);
        });
    }

    /**
     * 获取模块
     * @param moduleName 模块名称
     * @param moduleType 模块类型
     * @return 模块
     */
    public Modularity getModularity(String moduleType, String moduleName) {
        return MODULARITY_DATA.get(moduleType + ":" + moduleName);
    }
    /**
     * 获取模块
     * @param moduleId 模块类型:模块名称
     * @return 模块
     */
    public Modularity getModularity(String moduleId) {
        return MODULARITY_DATA.get(moduleId);
    }
    /**
     * 执行模块
     *
     * @param moduleName 模块名称
     * @param moduleType 模块类型
     * @param args       参数
     * @return 结果
     */
    public ModularityResult execute(String moduleType, String moduleName, Map<String, Object> args) {
        return execute(getModularity(moduleType, moduleName), args);
    }

    /**
     * 执行模块
     *
     * @param modularity 模块
     * @param args       参数
     * @return 结果
     */
    public ModularityResult execute(Modularity modularity, Map<String, Object> args) {
        ModularityExecutor<ModularityResult> executor = null;
        if (!modularity.hasDepends()) {
            executor = new DependsNullModularityExecutor(modularity, this);
        } else {
            executor = new DependsModularityExecutor(modularity, this);
        }

        return executor.execute(args);
    }

}
