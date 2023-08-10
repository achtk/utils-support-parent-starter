package com.chua.common.support.engine;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.spi.ServiceProvider;

/**
 * 引擎
 *
 * @author CH
 */
public interface Engine extends AutoCloseable {
    /**
     * 初始化引擎
     *
     * @param configuration 配置
     * @return 初始化引擎
     */
    static Engine auto(DetectionConfiguration configuration) {
        return ServiceProvider.of(Engine.class).getObjectProvider(configuration);
    }

    /**
     * 初始化引擎
     *
     * @param engine        名称
     * @param configuration 配置
     * @return 初始化引擎
     */
    static Engine of(String engine, DetectionConfiguration configuration) {
        return ServiceProvider.of(Engine.class).getNewExtension(engine, configuration);
    }

    /**
     * 获取实现
     *
     * @param target 目标类型
     * @param <T>    类型
     */
    <T> T get(Class<T> target);
}
