package com.chua.common.support.task.limit;


import com.chua.common.support.annotations.Spi;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 限流策略
 *
 * @author CH
 */
@Spi("token")
public interface LimiterProvider {
    /**
     * 初始化
     *
     * @return this
     */
    static LimiterProvider of() {
        return new TokenLimitProvider(10);
    }

    /**
     * 默认限流器配置
     *
     * @return this
     */
    default LimiterProvider defaultLimiterProvider() {
        return this.newLimiter("default", 10);
    }

    /**
     * 带大小的限流器
     *
     * @param size 最大限制
     * @param name 名称
     * @return this
     */
    LimiterProvider newLimiter(String name, double size);

    /**
     * 带大小的限流器
     *
     * @param size 最大限制
     * @return this
     */
    default LimiterProvider newLimiter(double size) {
        return newLimiter("default", size);
    }

    /**
     * 带大小的限流器
     *
     * @param config 最大限制
     * @return this
     */
    LimiterProvider newLimiter(Map<String, Integer> config);

    /**
     * 尝试获取
     *
     * @param name 资源
     * @return 尝试获取
     */
    boolean tryAcquire(String name);

    /**
     * 尝试获取
     *
     * @return 尝试获取
     */
    default boolean tryAcquire() {
        return tryAcquire("default");
    }

    /**
     * 尝试获取
     *
     * @param time 时间
     * @param name 资源
     * @return 尝试获取
     */
    boolean tryAcquire(String name, long time);

    /**
     * 尝试获取
     *
     * @param time     时间
     * @param name     资源
     * @param timeUnit 类型
     * @return 尝试获取
     */
    boolean tryAcquire(String name, long time, TimeUnit timeUnit);

    /**
     * 尝试入队
     *
     * @return 尝试入队
     */
    boolean tryGet();

    /**
     * 是否包含分组
     *
     * @param group 分组
     * @return 是否包含分组
     */
    boolean containGroup(String group);
}
