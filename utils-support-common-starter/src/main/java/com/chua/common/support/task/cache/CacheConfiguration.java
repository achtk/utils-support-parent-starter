package com.chua.common.support.task.cache;

import com.chua.common.support.bean.CustomMap;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 * 缓存配置
 *
 * @author CH
 */
@Getter
@Setter
@CustomMap
@Builder
public class CacheConfiguration {
    /**
     * 容量
     */
    @Builder.Default
    private int capacity = 100_000;
    /**
     * 超时时间
     */
    private int expireAfterWrite;
    /**
     * 冷热备份(即用的多的缓存数据生命周期增长)
     */
    @Builder.Default
    private boolean hotColdBackup = true;
    /**
     * 超时时间
     */
    @Builder.Default
    private int expireAfterAccess = -1;

}
