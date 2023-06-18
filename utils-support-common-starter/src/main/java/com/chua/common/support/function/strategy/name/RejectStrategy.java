package com.chua.common.support.function.strategy.name;

import com.chua.common.support.pojo.OssSystem;

/**
 * 拒绝策略
 *
 * @author CH
 * @since 2022/8/3 15:07
 */
public interface RejectStrategy {
    /**
     * 拒绝
     *
     * @param ossSystem ossSystem
     */
    byte[] reject(OssSystem ossSystem);
}
