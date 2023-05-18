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
     * 超时时间
     */
    private long expireAfterWrite;

}
