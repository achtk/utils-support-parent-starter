package com.chua.common.support.lang.arrange;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.modularity.Modularity;
import com.chua.common.support.utils.Preconditions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 编排
 * @author CH
 */
public class DelegateArrangeFactory implements ArrangeFactory, InitializingAware {

    static final Map<String, Arrange> MODULARITY_DATA = new ConcurrentHashMap<>();
    AtomicBoolean status = new AtomicBoolean(false);

    private DelegateArrangeFactory() {
    }

    /**
     * 初始化
     *
     * @return 初始化
     */
    public static DelegateArrangeFactory create() {
        DelegateArrangeFactory arrangeFactory = new DelegateArrangeFactory();
        arrangeFactory.afterPropertiesSet();
        return arrangeFactory;
    }

    /**
     * 注册模块
     *
     * @param modularity 模块
     * @return 结果
     */
    public DelegateArrangeFactory register(Modularity modularity) {
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
    public DelegateArrangeFactory unregister(Modularity modularity) {
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
    public DelegateArrangeFactory unregister(String moduleType, String moduleName) {
        MODULARITY_DATA.remove(moduleType + ":" + moduleName);
        return this;
    }

    @Override
    public void afterPropertiesSet() {
        if (status.get()) {
            return;
        }
        status.set(true);
    }

    /**
     * 获取模块
     *
     * @param moduleName 模块名称
     * @param moduleType 模块类型
     * @return 模块
     */
    public Arrange getArrange(String moduleType, String moduleName) {
        return MODULARITY_DATA.get(moduleType + ":" + moduleName);
    }

    /**
     * 获取模块
     *
     * @param moduleId 模块类型:模块名称
     * @return 模块
     */
    @Override
    public Arrange getArrange(String moduleId) {
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
    public ArrangeResult execute(String moduleType, String moduleName, Map<String, Object> args) {
        return execute(getArrange(moduleType, moduleName), args);
    }

    /**
     * 执行模块
     *
     * @param modularity 模块
     * @param args       参数
     * @return 结果
     */
    public ArrangeResult execute(Arrange modularity, Map<String, Object> args) {
        ArrangeExecutor<ArrangeResult> executor = null;
        if (!modularity.hasDepends()) {
            executor = new DependsNullArrangeExecutor(modularity);
        } else {
            executor = new DependsArrangeExecutor(modularity, this);
        }

        return executor.execute(args);
    }

}
