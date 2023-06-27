package com.chua.common.support.lang.arrange;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.utils.Preconditions;
import com.chua.common.support.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 编排
 * @author CH
 */
public class DelegateArrangeFactory implements ArrangeFactory, InitializingAware {

    final Map<String, Arrange> modularityData = new ConcurrentHashMap<>();
    AtomicBoolean status = new AtomicBoolean(false);
    private final ArrangeLogger arrangeLogger;

    private DelegateArrangeFactory(ArrangeLogger arrangeLogger) {
        this.arrangeLogger = ObjectUtils.defaultIfNull(arrangeLogger, (message, name, cost) -> {});
    }

    /**
     * 初始化
     *
     * @return 初始化
     */
    public static DelegateArrangeFactory create(ArrangeLogger arrangeLogger) {
        DelegateArrangeFactory arrangeFactory = new DelegateArrangeFactory(arrangeLogger);
        arrangeFactory.afterPropertiesSet();
        return arrangeFactory;
    }

    /**
     * 注册模块
     *
     * @param arrange 模块
     * @return 结果
     */
    public DelegateArrangeFactory register(Arrange arrange) {
        Preconditions.checkNotNull(arrange.getArrangeType());
        Preconditions.checkNotNull(arrange.getArrangeName());
        modularityData.put(arrange.getArrangeType() + ":" + arrange.getArrangeName(), arrange);
        return this;
    }

    /**
     * 注销模块
     *
     * @param arrange 模块
     * @return 结果
     */
    public DelegateArrangeFactory unregister(Arrange arrange) {
        modularityData.remove(arrange.getArrangeType() + ":" + arrange.getArrangeName());
        return this;
    }

    /**
     * 注销模块
     *
     * @param arrangeName 模块名称
     * @param arrangeType 模块类型
     * @return 结果
     */
    public DelegateArrangeFactory unregister(String arrangeType, String arrangeName) {
        modularityData.remove(arrangeType + ":" + arrangeName);
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
     * @param arrangeName 模块名称
     * @param arrangeType 模块类型
     * @return 模块
     */
    public Arrange getArrange(String arrangeType, String arrangeName) {
        return modularityData.get(arrangeType + ":" + arrangeName);
    }

    /**
     * 获取模块
     *
     * @param arrangeId 模块类型:模块名称
     * @return 模块
     */
    @Override
    public Arrange getArrange(String arrangeId) {
        return modularityData.get(arrangeId);
    }

    /**
     * 执行模块
     *
     * @param arrangeName 模块名称
     * @param arrangeType 模块类型
     * @param args       参数
     * @return 结果
     */
    public ArrangeResult execute(String arrangeType, String arrangeName, Map<String, Object> args) {
        return execute(getArrange(arrangeType, arrangeName), args);
    }

    /**
     * 执行模块
     *
     * @param arrange 模块
     * @param args       参数
     * @return 结果
     */
    public ArrangeResult execute(Arrange arrange, Map<String, Object> args) {
        ArrangeExecutor<ArrangeResult> executor = null;
        if (!arrange.hasDepends()) {
            executor = new DependsNullArrangeExecutor(arrange, arrangeLogger);
        } else {
            executor = new DependsArrangeExecutor(arrange, this, arrangeLogger);
        }

        return executor.execute(args);
    }

    @Override
    public ArrangeResult run(Map<String, Object> args) {
        ArrangeExecutor<ArrangeResult> executor = new DependsFindArrangeExecutor(this, arrangeLogger);
        return executor.execute(args);
    }

    @Override
    public List<Arrange> getNoDepends() {
        return modularityData.values().stream().filter(it -> StringUtils.isEmpty(it.getArrangeDepends())).collect(Collectors.toList());
    }

    @Override
    public List<Arrange> list() {
        return new ArrayList<>(modularityData.values());
    }

}
