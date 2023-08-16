package com.chua.common.support.lang.store;

import lombok.Data;

/**
 * 存儲配置
 * @author CH
 */
@Data
public class StoreConfig {
    /**
     * 保留天数
     */
    private int retentionDays = 3;
    /**
     * 是否压缩
     */
    private boolean compression = true;
}
