package com.chua.common.support.database.transfer;

import lombok.Builder;
import lombok.Data;

/**
 * 基础配置
 * @author CH
 */
@Builder
@Data
public class SinkConfig {
    /**
     * 数据开始位置
     */
    @Builder.Default
    private int offset = 0;
    /**
     * 限制数量
     */
    @Builder.Default
    private int limit = 1000;
}
