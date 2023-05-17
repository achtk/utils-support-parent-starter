package com.chua.common.support.protocol.discovery;

/**
 * 绑定类型
 *
 * @author CH
 * @version 1.0.0
 */
public enum DiscoveryBoundType {
    /**
     * 轮询（Round Robin）法
     */
    ROUND_ROBIN(false),
    /**
     * 随机（Random）法
     */
    RANDOM_ROBIN(false),
    /**
     * 源地址哈希（Hash）法
     */
    HASH_ROBIN(false),
    /**
     * 加权随机算法
     */
    WEIGHT_RANDOM_ROBIN(false);
    final boolean inclusive;

    DiscoveryBoundType(boolean inclusive) {
        this.inclusive = inclusive;
    }
}
