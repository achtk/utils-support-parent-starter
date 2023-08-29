package com.chua.common.support.engine;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.StringUtils;

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
        if (StringUtils.isEmpty(configuration.engine())) {
            return ServiceProvider.of(Engine.class).getObjectProvider(configuration);
        }
        return ServiceProvider.of(Engine.class).getNewExtension(configuration.engine(), configuration);
    }

    /**
     * 初始化引擎
     *
     * @param configuration 配置
     * @return 初始化引擎
     */
    static Engine of(DetectionConfiguration configuration) {
        return auto(configuration);
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
     * @return 目标类实现
     */
    default <T> T get(Class<T> target) {
        return get(null, target);
    }

    /**
     * 获取实现
     *
     * @param name   实现
     * @param target 目标类型
     * @param <T>    类型
     * @return 目标类实现
     */
    <T> T get(String name, Class<T> target);
}
